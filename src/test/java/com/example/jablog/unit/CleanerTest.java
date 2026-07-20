package com.example.jablog.unit;

import com.example.jablog.entity.Board;
import com.example.jablog.entity.Posts;
import com.example.jablog.entity.Threads;
import com.example.jablog.entity.Users;
import com.example.jablog.repository.CleanerRepository;
import com.example.jablog.service.Cleaner;
import com.example.jablog.service.MinioService;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CleanerTest {

    @Mock
    private CleanerRepository cleanerRepository;

    @Mock
    private MinioService minioService;

    @InjectMocks
    private Cleaner cleaner;

    @Test
    public void cleanOldThreads_Success(){
        when(cleanerRepository.threads(anyLong())).thenReturn(List.of(
                createThread("pic1"),
                createThread("pic2")
        ));

        cleaner.cleanThreads();

        verify(cleanerRepository).threads(anyLong());
        verify(minioService, times(2)).deletePicture(anyString(),MinioService.BUCKET);
        verifyNoMoreInteractions(minioService);
    }

    @Test
    public void cleanOldPosts_Success(){
        when(cleanerRepository.posts()).thenReturn(List.of(
                createPost("pic1"),
                createPost("pic2"),
                createPost(null),
                createPost(null),
                createPost(""),
                createPost("")
        ));

        cleaner.cleanPosts();

        verify(cleanerRepository).posts();
        verify(minioService, times(2)).deletePicture(anyString(), MinioService.BUCKET);
        verifyNoMoreInteractions(minioService);
    }

    @Test
    public void cleanPics_Success(){
        when(cleanerRepository.pics()).thenReturn(Arrays.asList(
                "pic1",
                "pic2",
                null,
                null,
                "",
                ""
        ));

        when(minioService.getAllFileName(MinioService.BUCKET)).thenReturn(List.of(
                "pic1",
                "pic2",
                "pic3",
                "pic4",
                "pic5",
                "pic6"
        ));

        cleaner.cleanPics();

        verify(minioService).getAllFileName(MinioService.BUCKET);
        verify(cleanerRepository).pics();
        verify(minioService, times(4)).deletePicture(anyString(), MinioService.BUCKET);
        verifyNoMoreInteractions(minioService);
    }

    @Test
    public void cleanBoards_Success(){
        when(cleanerRepository.boards(anyLong())).thenReturn(List.of(
                crateBoard(),
                crateBoard()
        ));

        cleaner.cleanBoards();

        verify(cleanerRepository).boards(anyLong());
    }

    @NotNull
    private Threads createThread(@NotNull String picName){
        Threads res = new Threads();
        res.setBoard(new Board());
        res.setPicture(picName);
        return res;
    }

    @NotNull
    private Posts createPost(@Nullable String picName){
        Posts res = new Posts();
        res.setPicture(picName);
        return res;
    }

    @NotNull
    private Board crateBoard(){
        Board res = new Board();
        res.setName("tst");
        Set<Users> users = new HashSet<Users>();
        users.add(new Users());
        res.setUsers(users);
        return res;
    }
}
