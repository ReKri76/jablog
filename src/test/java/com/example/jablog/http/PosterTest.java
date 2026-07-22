package com.example.jablog.http;

import com.example.jablog.DTO.Picture;
import com.example.jablog.DTO.Post;
import com.example.jablog.controllers.Poster;
import com.example.jablog.service.CustomUserDetailsService;
import com.example.jablog.service.PosterService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class PosterTest {

    @Mock
    private PosterService posterService;

    @Mock
    private CustomUserDetailsService customUserDetailsService;

    @InjectMocks
    private Poster posterController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(posterController).build();
    }

    private final String DEFAULT_BODY = "default_body";
    private final String DEFAULT_HEAD = "default_header";

    @Test
    void thread_Success() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "image", "test.png", "image/png", "dummy content".getBytes()
        );

        when(posterService.thread(any(Post.class), any(Picture.class), eq("b"))).thenReturn(42L);

        mockMvc.perform(multipart("/poster/b")
                        .file(file)
                        .param("head", DEFAULT_HEAD)
                        .param("body", DEFAULT_BODY))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/b/42"));

        verify(posterService).thread(any(Post.class), any(Picture.class), eq("b"));
    }

    @Test
    void thread_EmptyFile_ReturnsBadRequest() throws Exception {
        MockMultipartFile emptyFile = new MockMultipartFile("image", "", "image/png", new byte[0]);

        mockMvc.perform(multipart("/poster/b").file(emptyFile))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(posterService);
    }

    @Test
    void thread_InvalidFileType_ReturnsBadRequest() throws Exception {
        MockMultipartFile pdfFile = new MockMultipartFile(
                "image", "doc.pdf", "application/pdf", "dummy pdf".getBytes()
        );

        mockMvc.perform(multipart("/poster/b").file(pdfFile))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(posterService);
    }

    @Test
    void thread_FileTooLarge_ReturnsBadRequest() throws Exception {
        byte[] oversizedContent = new byte[(int) Poster.MAX_IMAGE_SIZE + 10];
        MockMultipartFile hugeFile = new MockMultipartFile(
                "image", "huge.jpg", "image/jpeg", oversizedContent
        );

        mockMvc.perform(multipart("/poster/b").file(hugeFile))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(posterService);
    }

    @Test
    void post_WithImage_WithoutHeader_Success() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "image", "pic.jpeg", "image/jpeg", "image data".getBytes()
        );

        mockMvc.perform(multipart("/poster/b/100")
                        .file(file)
                        .param("body", DEFAULT_BODY))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/b/100"));

        verify(posterService).post(any(Post.class), any(Picture.class), eq(100L));
    }

    @Test
    void post_WithoutImage_WithHeader_Success() throws Exception {
        mockMvc.perform(multipart("/poster/b/100")
                        .param("head", DEFAULT_HEAD)
                        .param("body", DEFAULT_BODY))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/b/100"));

        Post post = new Post();
        post.setBody(DEFAULT_BODY);
        post.setHead(DEFAULT_HEAD);

        verify(posterService).post(post, null, 100L);
    }

    @Test
    void board_Success() throws Exception {
        MockHttpSession session = new MockHttpSession();
        UserDetails mockUser = new User("admin", "password", Collections.emptyList());

        when(customUserDetailsService.loadUserByUsername("admin")).thenReturn(mockUser);

        mockMvc.perform(post("/poster/board")
                        .session(session)
                        .param("boardName", "tech")
                        .param("rule", "No spam")
                        .param("pass", "secret123")
                        .param("nickname", "admin")
                        .param("lifeCycleThreads", "5")
                        .param("lifeCyclePosts", "50")
                        .param("transcription", "Technology"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/tech"));

        verify(posterService, times(1)).board(
                "tech", "secret123", "No spam", "admin", 5, 50, "Technology"
        );
        verify(customUserDetailsService, times(1)).loadUserByUsername("admin");

        assertEquals(mockUser, session.getAttribute("tech"));
    }
}