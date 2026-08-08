package io.rekri.jablog.unit;

import io.rekri.jablog.DTO.PostWithPicture;
import io.rekri.jablog.entity.Board;
import io.rekri.jablog.entity.Posts;
import io.rekri.jablog.entity.Threads;
import io.rekri.jablog.repository.GetterRepository;
import io.rekri.jablog.service.CustomUserDetailsService;
import io.rekri.jablog.service.GetterService;
import io.rekri.jablog.service.MinioService;
import io.rekri.jablog.service.security.DeleterAccessService;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class GetterServiceTest {

    @Mock
    private GetterRepository getterRepository;
    @Mock
    private DeleterAccessService deleterAccessService;
    @Mock
    private CustomUserDetailsService customUserDetailsService;
    @Mock
    private MinioService minioService;

    private final String DEFAULT_BOARD_NAME = "tst";
    private final String DEFAULT_TRANSCRIPTION = "test";
    private final String DEFAULT_CONTENT = "test_content";
    private final String DEFAULT_URL = "test_url";
    private final String DEFAULT_HEAD = "test_head";

    @InjectMocks
    private GetterService getterService;

    @Test
    public void start_Success() {
        when(getterRepository.start()).thenReturn((ArrayList<Board>) createList(createBoard(), 4));

        List<io.rekri.jablog.DTO.Board> res = getterService.start();

        res.forEach(board -> {
            assertEquals(DEFAULT_BOARD_NAME, board.getId());
            assertEquals(DEFAULT_TRANSCRIPTION, board.getTranscription());
        });
        verify(getterRepository).start();
    }

    @Test
    public void board_Success() {
        when(getterRepository.board(anyString(), anyInt())).thenReturn((ArrayList<Threads>) createList(createThread(), 4));
        when(minioService.buildPictureUrl(anyString(), anyString())).thenReturn(DEFAULT_URL);

        List<PostWithPicture> res = getterService.board("test", 0);

        res.forEach(thread -> {
            assertEquals(DEFAULT_URL, thread.getUrl());
            assertEquals(DEFAULT_HEAD, thread.getHead());
            assertEquals(DEFAULT_CONTENT, thread.getBody());
        });
        verify(getterRepository).board(anyString(), anyInt());
    }

    @Test
    public void thread_Success() {

        Threads mockThread = createThread();
        mockThread.setBoard(createBoard());
        TreeSet<Posts> mockPosts = new TreeSet<>();
        mockPosts.add(createPostsWithPicture(null));
        mockPosts.add(createPostsWithPicture(null));
        mockPosts.add(createPostsWithPicture(""));
        mockPosts.add(createPostsWithPicture(""));
        mockPosts.add(createPostsWithPicture("pic1"));
        mockPosts.add(createPostsWithPicture("pic2"));
        mockThread.setPosts(mockPosts);

        when(getterRepository.thread(anyLong())).thenReturn(mockThread);
        when(minioService.buildPictureUrl(anyString(), anyString())).thenReturn(DEFAULT_URL);

        List<PostWithPicture> res = getterService.thread(0);
        PostWithPicture main = res.getFirst();
        assertEquals(DEFAULT_URL, main.getUrl());
        assertEquals(DEFAULT_HEAD, main.getHead());
        assertEquals(DEFAULT_CONTENT, main.getBody());
        res.removeFirst();
        res.forEach(postBase -> {
            assertEquals(DEFAULT_HEAD, postBase.getHead());
            assertEquals(DEFAULT_CONTENT, postBase.getBody());
            assertTrue(postBase.getUrl() == null || postBase.getUrl().equals(DEFAULT_URL));
        });
        verify(getterRepository).thread(anyLong());
    }

    @NotNull
    private Board createBoard(){
        Board board = new Board();
        board.setTranscription(DEFAULT_TRANSCRIPTION);
        board.setName(DEFAULT_BOARD_NAME);
        return board;
    }

    @NotNull
    private Threads createThread(){
        Threads res = new Threads();
        res.setId(0);
        res.setContent(DEFAULT_CONTENT);
        res.setHeader(DEFAULT_HEAD);
        res.setPicture(DEFAULT_URL);
        return res;
    }

    @NotNull
    private Posts createPostsWithPicture(@Nullable String picname){
        Posts res = new Posts();
        res.setId(0);
        res.setContent(DEFAULT_CONTENT);
        res.setHeader(DEFAULT_HEAD);
        res.setPicture(picname);
        return res;
    }

    @NotNull
    private<E> List<E> createList(E entity, int size){
        List<E> list = new ArrayList<>();
        for (int i=0;i<size;i++){
            list.add(entity);
        }
        return list;
    }
}
