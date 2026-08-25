package io.rekri.jablog.http;

import io.rekri.jablog.DTO.BoardToCreate;
import io.rekri.jablog.DTO.Picture;
import io.rekri.jablog.DTO.Post;
import io.rekri.jablog.controllers.PosterController;
import io.rekri.jablog.service.PosterService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class PosterControllerTest {

    @Mock
    private PosterService posterService;

    @InjectMocks
    private PosterController posterController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(posterController).build();
    }

    private final String DEFAULT_BODY = "default_body";
    private final String DEFAULT_HEAD = "default_header";

    @Captor
    private ArgumentCaptor<BoardToCreate> boardToCreateArgumentCaptor;

    @Captor
    private ArgumentCaptor<String> stringArgumentCaptor;

    private MockMultipartFile postPart(String head, String body) {
        String content = String.format(
                "{\"head\":\"%s\",\"body\":\"%s\"}",
                head, body
        );

        return new MockMultipartFile(
                "post", "", "application/json", content.getBytes()
        );
    }

    @Test
    void thread_Success() throws Exception {
        MockMultipartFile post = postPart(DEFAULT_HEAD, DEFAULT_BODY);
        MockMultipartFile file = new MockMultipartFile(
                "image", "test.png", "image/png", "dummy content".getBytes()
        );

        when(posterService.thread(any(Post.class), any(Picture.class), eq("b"))).thenReturn(42L);

        mockMvc.perform(multipart("/api/poster/b")
                        .file(post)
                        .file(file))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/b/42"));

        verify(posterService).thread(any(Post.class), any(Picture.class), eq("b"));
    }

    @Test
    void thread_EmptyFile_ReturnsBadRequest() throws Exception {
        MockMultipartFile post = postPart(DEFAULT_HEAD, DEFAULT_BODY);
        MockMultipartFile emptyFile = new MockMultipartFile(
                "image", "", "image/png", new byte[0]
        );

        mockMvc.perform(multipart("/api/poster/b")
                        .file(post)
                        .file(emptyFile))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(posterService);
    }

    @Test
    void thread_InvalidFileType_ReturnsBadRequest() throws Exception {
        MockMultipartFile post = postPart(DEFAULT_HEAD, DEFAULT_BODY);
        MockMultipartFile pdfFile = new MockMultipartFile(
                "image", "doc.pdf", "application/pdf", "dummy pdf".getBytes()
        );

        mockMvc.perform(multipart("/api/poster/b")
                        .file(post)
                        .file(pdfFile))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(posterService);
    }

    @Test
    void thread_FileTooLarge_ReturnsBadRequest() throws Exception {
        MockMultipartFile post = postPart(DEFAULT_HEAD, DEFAULT_BODY);
        byte[] oversizedContent = new byte[(int) PosterController.MAX_IMAGE_SIZE + 10];
        MockMultipartFile hugeFile = new MockMultipartFile(
                "image", "huge.jpg", "image/jpeg", oversizedContent
        );

        mockMvc.perform(multipart("/api/poster/b")
                        .file(post)
                        .file(hugeFile))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(posterService);
    }

    @Test
    void post_WithImage_WithoutHeader_Success() throws Exception {
        MockMultipartFile post = postPart("", DEFAULT_BODY);
        MockMultipartFile file = new MockMultipartFile(
                "image", "pic.jpeg", "image/jpeg", "image data".getBytes()
        );

        mockMvc.perform(multipart("/api/poster/b/100")
                        .file(post)
                        .file(file))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/b/100"));

        verify(posterService).post(any(Post.class), any(Picture.class), eq(100L));
    }

    @Test
    void post_WithoutImage_WithHeader_Success() throws Exception {
        MockMultipartFile post = postPart(DEFAULT_HEAD, DEFAULT_BODY);

        mockMvc.perform(multipart("/api/poster/b/100")
                        .file(post))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/b/100"));

        Post expectedPost = new Post();
        expectedPost.setBody(DEFAULT_BODY);
        expectedPost.setHead(DEFAULT_HEAD);

        verify(posterService).post(expectedPost, null, 100L);
    }

    @Test
    void board_Success() throws Exception {

        final String nickname = "placeholder";
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        Authentication auth = new UsernamePasswordAuthenticationToken(nickname, null, List.of());
        context.setAuthentication(auth);
        SecurityContextHolder.setContext(context);

        BoardToCreate expectedBody = new BoardToCreate(
                "tec",
                "rwdxrwdxrwdx",
                "secret123",
                "admin",
                10,
                5,
                "Technology"
        );

        String board = """
                {
                    "boardName": "tec",
                    "rule": "rwdxrwdxrwdx",
                    "pass": "secret123",
                    "nickname": "admin",
                    "lifeCycleThreads": 10,
                    "lifeCyclePosts": 5,
                    "transcription": "Technology"
                }
                """;

        try {
            mockMvc.perform(post("/api/poster/board")
                            .with(authentication(auth))
                            .contentType("application/json")
                            .content(board))
                    .andExpect(status().isCreated())
                    .andExpect(header().string("Location", "/tec"));

            verify(posterService).board(boardToCreateArgumentCaptor.capture(), stringArgumentCaptor.capture());

            assertEquals(expectedBody, boardToCreateArgumentCaptor.getValue());
            assertEquals(nickname, stringArgumentCaptor.getValue());
        } finally {
            SecurityContextHolder.clearContext();
        }
    }
}