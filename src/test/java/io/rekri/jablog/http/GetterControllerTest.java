package io.rekri.jablog.http;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import io.rekri.jablog.DTO.Board;
import io.rekri.jablog.DTO.PostWithPicture;
import io.rekri.jablog.config.security.CustomUserDetails;
import io.rekri.jablog.controllers.GetterController;
import io.rekri.jablog.service.GetterService;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class GetterControllerTest {

    @Mock
    private GetterService getterService;

    @InjectMocks
    private GetterController getterController;

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    final private String DEFAULT_BOARD_NAME = "boardName";

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(getterController).build();
    }

    @Test
    public void thread_HaventAccessToDelete_Success() throws Exception {
        long threadId = 1;
        final ArrayList<PostWithPicture> originalPosts = new ArrayList<>();
        for (int i = 0; i < 10; i++)
            originalPosts.add(createPostWithPicture(i));
        when(getterService.thread(threadId)).thenReturn(new ArrayList<>(originalPosts));

        PostWithPicture expectedThread = originalPosts.getFirst();
        List<PostWithPicture> expectedReplies = originalPosts.subList(1, originalPosts.size());

        CustomUserDetails mockUser = Mockito.mock(CustomUserDetails.class);
        MockHttpSession session = new MockHttpSession();
        session.setAttribute(DEFAULT_BOARD_NAME, mockUser);

        String expectedThreadIdString = String.valueOf(expectedThread.getId());
        when(getterService.canDelete(DEFAULT_BOARD_NAME, mockUser, expectedThreadIdString)).thenReturn(false);

        mockMvc.perform(get("/api/" + DEFAULT_BOARD_NAME + "/" + threadId)
                        .session(session)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("ok"))
                .andExpect(jsonPath("$.boardName").value(DEFAULT_BOARD_NAME))
                .andExpect(jsonPath("$.thread").value(toJsonPathObject(expectedThread)))
                .andExpect(jsonPath("$.posts").value(toJsonPathObject(expectedReplies)))
                .andExpect(jsonPath("$.canDelete").value(false));

        verify(getterService).thread(threadId);
    }

    @Test
    public void thread_HaveAccessToDelete_Success() throws Exception {
        long threadId = 1;
        final ArrayList<PostWithPicture> originalPosts = new ArrayList<>();
        for (int i = 0; i < 10; i++)
            originalPosts.add(createPostWithPicture(i));
        when(getterService.thread(threadId)).thenReturn(new ArrayList<>(originalPosts));

        PostWithPicture expectedThread = originalPosts.getFirst();
        List<PostWithPicture> expectedReplies = originalPosts.subList(1, originalPosts.size());

        CustomUserDetails mockUser = Mockito.mock(CustomUserDetails.class);
        MockHttpSession session = new MockHttpSession();
        session.setAttribute(DEFAULT_BOARD_NAME, mockUser);

        String expectedThreadIdString = String.valueOf(expectedThread.getId());
        when(getterService.canDelete(DEFAULT_BOARD_NAME, mockUser, expectedThreadIdString)).thenReturn(true);

        mockMvc.perform(get("/api/" + DEFAULT_BOARD_NAME + "/" + threadId)
                        .session(session)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("ok"))
                .andExpect(jsonPath("$.boardName").value(DEFAULT_BOARD_NAME))
                .andExpect(jsonPath("$.thread").value(toJsonPathObject(expectedThread)))
                .andExpect(jsonPath("$.posts").value(toJsonPathObject(expectedReplies)))
                .andExpect(jsonPath("$.canDelete").value(true));

        verify(getterService).thread(threadId);
    }

    @Test
    public void board_HaventAccessToDelete_Success() throws Exception {
        final ArrayList<PostWithPicture> posts = new ArrayList<>();
        for (int i = 0; i < 10; i++)
            posts.add(createPostWithPicture(i));
        when(getterService.board(DEFAULT_BOARD_NAME, 0)).thenReturn(posts);

        CustomUserDetails mockUser = Mockito.mock(CustomUserDetails.class);
        MockHttpSession session = new MockHttpSession();
        session.setAttribute(DEFAULT_BOARD_NAME, mockUser);
        when(getterService.canDelete(DEFAULT_BOARD_NAME, mockUser, null)).thenReturn(false);

        mockMvc.perform(get("/api/" + DEFAULT_BOARD_NAME)
                        .session(session)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("ok"))
                .andExpect(jsonPath("$.boardName").value(DEFAULT_BOARD_NAME))
                .andExpect(jsonPath("$.threads").value(toJsonPathObject(posts)))
                .andExpect(jsonPath("$.canDelete").value(false));

        verify(getterService).board(DEFAULT_BOARD_NAME, 0);
    }

    @Test
    public void board_HaveAccessToDelete_Success() throws Exception {
        final ArrayList<PostWithPicture> posts = new ArrayList<>();
        for (int i = 0; i < 10; i++)
            posts.add(createPostWithPicture(i));
        when(getterService.board(DEFAULT_BOARD_NAME, 0)).thenReturn(posts);

        CustomUserDetails mockUser = Mockito.mock(CustomUserDetails.class);
        MockHttpSession session = new MockHttpSession();
        session.setAttribute(DEFAULT_BOARD_NAME, mockUser);
        when(getterService.canDelete(DEFAULT_BOARD_NAME, mockUser, null)).thenReturn(true);

        mockMvc.perform(get("/api/" + DEFAULT_BOARD_NAME)
                        .session(session)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("ok"))
                .andExpect(jsonPath("$.boardName").value(DEFAULT_BOARD_NAME))
                .andExpect(jsonPath("$.threads").value(toJsonPathObject(posts)))
                .andExpect(jsonPath("$.canDelete").value(true));

        verify(getterService).board(DEFAULT_BOARD_NAME, 0);
    }

    @Test
    public void start() throws Exception {
        final ArrayList<Board> boards = new ArrayList<>();
        for (int i = 0; i < 10; i++)
            boards.add(createBoard(Integer.toString(i)));
        when(getterService.start()).thenReturn(boards);

        mockMvc.perform(get("/api/"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("ok"))
                .andExpect(jsonPath("$.boards").value(toJsonPathObject(boards)));
        verify(getterService).start();
    }

    @NotNull
    public PostWithPicture createPostWithPicture(int id){
        PostWithPicture res = new PostWithPicture();
        res.setId(id);
        res.setBody("placeholder");
        return res;
    }

    @NotNull
    public Board createBoard(String id){
        Board res = new Board();
        res.setId(id);
        return res;
    }

    @NotNull
    private Object toJsonPathObject(@NotNull Object obj) throws Exception {
        String json = objectMapper.writeValueAsString(obj);
        return JsonPath.parse(json).read("$");
    }
}