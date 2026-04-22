package com.example.jablog.controllers;

import com.example.jablog.DTO.PostWithPicture;
import com.example.jablog.config.security.CustomUserDetails;
import com.example.jablog.service.GetterService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

class GetterControllerTest {

    private MockMvc mockMvc;
    private GetterService getterService;

    @BeforeEach
    void setUp() {
        getterService = mock(GetterService.class);
        mockMvc = MockMvcBuilders.standaloneSetup(new Getter(getterService)).build();
    }

    @Test
    void startShouldRenderIndexWithBoards() throws Exception {
        final ArrayList<String> boards = new ArrayList<>();
        boards.add("b");
        boards.add("a");
        when(getterService.start()).thenReturn(boards);

        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(model().attribute("boards", boards));
    }

    @Test
    void boardShouldRenderBoardPageWithDeleteFlagAndFormModel() throws Exception {
        final ArrayList<PostWithPicture> threads = new ArrayList<>();
        final PostWithPicture thread = new PostWithPicture();
        thread.setHead("head");
        thread.setBody("body");
        thread.setUrl("http://img");
        threads.add(thread);

        final MockHttpSession session = new MockHttpSession();
        final CustomUserDetails user = new CustomUserDetails("b", "rwdxrw-xr---", "pass", "mod", "ROLE_ADMIN");
        session.setAttribute("b", user);

        when(getterService.board("b", 2)).thenReturn(threads);
        when(getterService.canDelete("b", user, false)).thenReturn(true);

        mockMvc.perform(get("/b").param("page", "2").session(session))
                .andExpect(status().isOk())
                .andExpect(view().name("board"))
                .andExpect(model().attribute("threads", threads))
                .andExpect(model().attribute("boardName", "b"))
                .andExpect(model().attributeExists("post"))
                .andExpect(model().attribute("canDelete", true));
    }

    @Test
    void threadShouldSplitMainPostFromReplies() throws Exception {
        final PostWithPicture main = new PostWithPicture();
        main.setId(10L);
        main.setHead("main");
        main.setBody("body");
        main.setUrl("http://img/main");

        final PostWithPicture reply = new PostWithPicture();
        reply.setId(11L);
        reply.setHead("reply");
        reply.setBody("reply body");
        reply.setUrl("http://img/reply");

        final ArrayList<PostWithPicture> posts = new ArrayList<>();
        posts.add(main);
        posts.add(reply);

        final MockHttpSession session = new MockHttpSession();

        when(getterService.thread(10L, "b")).thenReturn(posts);
        when(getterService.canDelete("b", null, true)).thenReturn(false);

        mockMvc.perform(get("/b/10").session(session))
                .andExpect(status().isOk())
                .andExpect(view().name("thread"))
                .andExpect(model().attribute("thread", main))
                .andExpect(model().attribute("posts", posts))
                .andExpect(model().attribute("boardName", "b"))
                .andExpect(model().attributeExists("post"))
                .andExpect(model().attribute("canDelete", false));

        verify(getterService).canDelete("b", null, true);
    }
}
