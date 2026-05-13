package com.example.jablog.service;

import com.example.jablog.DTO.Picture;
import com.example.jablog.DTO.Post;
import com.example.jablog.entity.Board;
import com.example.jablog.entity.Posts;
import com.example.jablog.entity.Threads;
import com.example.jablog.repository.PosterRepository;
import jakarta.persistence.EntityManager;
import org.hibernate.Session;
import org.hibernate.SimpleNaturalIdLoadAccess;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PosterServiceTest {

    @Mock
    private PosterRepository posterRepository;
    @Mock
    private EntityManager entityManager;
    @Mock
    private MinioService minioService;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private Session session;
    @Mock
    private SimpleNaturalIdLoadAccess<Board> naturalIdLoadAccess;

    private PosterService posterService;

    @BeforeEach
    void setUp() {
        posterService = new PosterService(posterRepository, entityManager, minioService, passwordEncoder);
    }

    @Test
    void threadShouldUploadPicturePersistThreadAndTrimTrailingSlashInEndpoint() {
        final Post post = new Post();
        post.setHead("");
        post.setBody("message");

        final Picture picture = picture("cat.jpg");
        final Board boardRef = new Board();
        boardRef.setName("b");

        when(entityManager.unwrap(Session.class)).thenReturn(session);
        when(session.bySimpleNaturalId(Board.class)).thenReturn(naturalIdLoadAccess);
        when(naturalIdLoadAccess.getReference("b")).thenReturn(boardRef);
        when(posterRepository.thread(any(Threads.class))).thenReturn(15L);
        when(minioService.buildPictureUrl(anyString())).thenAnswer(invocation ->
                "http://localhost:9000/images/" + invocation.getArgument(0, String.class));

        final long result = posterService.thread(post, picture, "b");

        assertThat(result).isEqualTo(15L);
        assertThat(post.getHead()).isEqualTo("message");
        verify(minioService).savePicture(picture, "images");

        final ArgumentCaptor<Threads> threadCaptor = ArgumentCaptor.forClass(Threads.class);
        verify(posterRepository).thread(threadCaptor.capture());

        final Threads saved = threadCaptor.getValue();
        assertThat(saved.getHeader()).isEqualTo("message");
        assertThat(saved.getContent()).isEqualTo("message");
        assertThat(saved.getBoard()).isSameAs(boardRef);
        assertThat(saved.getPicture())
                .startsWith("http://localhost:9000/images/")
                .contains("cat.jpg");
    }

    @Test
    void postWithoutImageShouldPersistEmptyPictureAndSkipMinioUpload() {
        final Post post = new Post();
        post.setHead("head");
        post.setBody("message");

        final Threads threadRef = new Threads();
        when(posterRepository.getThreadsById(42L)).thenReturn(threadRef);

        posterService.post(post, null, 42L);

        verify(minioService, never()).savePicture(any(Picture.class), any(String.class));

        final ArgumentCaptor<Posts> postCaptor = ArgumentCaptor.forClass(Posts.class);
        verify(posterRepository).post(postCaptor.capture());

        final Posts saved = postCaptor.getValue();
        assertThat(saved.getHeader()).isEqualTo("head");
        assertThat(saved.getContent()).isEqualTo("message");
        assertThat(saved.getPicture()).isEmpty();
        assertThat(saved.getThread()).isSameAs(threadRef);
    }

    @Test
    void postWithImageShouldUploadPictureAndPersistBuiltUrl() {
        final Post post = new Post();
        post.setHead("head");
        post.setBody("message");

        final Picture picture = picture("cat.jpg");
        final Threads threadRef = new Threads();
        when(posterRepository.getThreadsById(42L)).thenReturn(threadRef);
        when(minioService.buildPictureUrl(anyString())).thenAnswer(invocation ->
                "http://localhost:9000/images/" + invocation.getArgument(0, String.class));

        posterService.post(post, picture, 42L);

        verify(minioService).savePicture(picture, "images");

        final ArgumentCaptor<Posts> postCaptor = ArgumentCaptor.forClass(Posts.class);
        verify(posterRepository).post(postCaptor.capture());

        final Posts saved = postCaptor.getValue();
        assertThat(saved.getHeader()).isEqualTo("head");
        assertThat(saved.getContent()).isEqualTo("message");
        assertThat(saved.getPicture())
                .startsWith("http://localhost:9000/images/")
                .contains("cat.jpg");
        assertThat(saved.getThread()).isSameAs(threadRef);
    }

    @Test
    void boardShouldRejectWhenPostLifecycleIsNotShorterThanThreadLifecycle() {
        assertThatThrownBy(() -> posterService.board("b", "pass", "rwdxrwdxrwdx", "mod", 10, 10))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("life cycle of posts cant be longer then threads");

        verify(posterRepository, never()).board(any(Board.class), any());
    }

    @Test
    void boardShouldRejectNegativePostLifecycle() {
        assertThatThrownBy(() -> posterService.board("b", "pass", "rwdxrwdxrwdx", "mod", 10, -1))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("value of life cycle must be positive");

        verify(posterRepository, never()).board(any(Board.class), any());
    }

    @Test
    void boardShouldRejectRuleWithWrongLength() {
        assertThatThrownBy(() -> posterService.board("b", "pass", "rwx", "mod", 10, 5))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("incorrect rule");

        verify(posterRepository, never()).board(any(Board.class), any());
    }

    private Picture picture(String name) {
        final MockMultipartFile file = new MockMultipartFile(
                "image",
                name,
                "image/jpeg",
                "image".getBytes()
        );

        try {
            return new Picture(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
