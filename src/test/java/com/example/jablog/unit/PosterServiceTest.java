package com.example.jablog.unit;

import com.example.jablog.DTO.Picture;
import com.example.jablog.DTO.Post;
import com.example.jablog.entity.Board;
import com.example.jablog.entity.Posts;
import com.example.jablog.entity.Threads;
import com.example.jablog.entity.Users;
import com.example.jablog.errors.InvalidRulesException;
import com.example.jablog.repository.PosterRepository;
import com.example.jablog.service.MinioService;
import com.example.jablog.service.PosterService;
import jakarta.persistence.EntityManager;
import org.hibernate.Session;
import org.hibernate.SimpleNaturalIdLoadAccess;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PosterServiceTest {
    @Mock
    private PosterRepository posterRepository;
    @Mock
    private EntityManager entityManager;
    @Mock
    private MinioService minioService;
    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private PosterService posterService;

    final private static String DEFAULT_HEADER = "head_default";
    final private static String DEFAULT_CONTENT = "content_default";
    final private static String DEFAULT_PICTURE = "picture_default";
    final private static String DEFAULT_BOARD = "board_default";

    @Captor
    private final ArgumentCaptor<Threads> threadsCaptor = ArgumentCaptor.forClass(Threads.class);
    @Captor
    private final ArgumentCaptor<Posts> postsCaptor = ArgumentCaptor.forClass(Posts.class);
    @Captor
    private final ArgumentCaptor<Board> boardCaptor = ArgumentCaptor.forClass(Board.class);
    @Captor
    private final ArgumentCaptor<Users> usersCaptor = ArgumentCaptor.forClass(Users.class);

    @Test
    public void thread_GettingPostWithHead_Success() {
        final Post mockPost = new Post();
        mockPost.setHead(DEFAULT_HEADER);
        mockPost.setBody(DEFAULT_CONTENT);

        final Board mockBoard = new Board();
        mockBoard.setName(DEFAULT_BOARD);
        final Session mockSession = mock(Session.class);
        final SimpleNaturalIdLoadAccess naturalIdLoadAccess = mock(SimpleNaturalIdLoadAccess.class);

        when(entityManager.unwrap(Session.class)).thenReturn(mockSession);
        when(mockSession.bySimpleNaturalId(Board.class)).thenReturn(naturalIdLoadAccess);
        when(naturalIdLoadAccess.getReference(DEFAULT_BOARD)).thenReturn(mockBoard);

        final Picture mockPicture = new Picture();
        mockPicture.setName(DEFAULT_PICTURE);
        doNothing().when(minioService).savePicture(mockPicture, MinioService.BUCKET);

        when(posterRepository.thread(any(Threads.class))).thenReturn(0L);

        posterService.thread(mockPost, mockPicture, DEFAULT_BOARD);

        verify(minioService).savePicture(mockPicture, MinioService.BUCKET);
        verify(posterRepository).thread(threadsCaptor.capture());
        final Threads threads = threadsCaptor.getValue();
        assertEquals(DEFAULT_HEADER, threads.getHeader());
        assertEquals(DEFAULT_CONTENT, threads.getContent());
        assertEquals(DEFAULT_PICTURE, threads.getPicture());
        assertEquals(DEFAULT_BOARD, threads.getBoard().getName());
    }

    @Test
    public void thread_GettingPostWithoutHead_Success() {
        final Post mockPost = new Post();
        mockPost.setBody(DEFAULT_CONTENT);

        final Board mockBoard = new Board();
        mockBoard.setName(DEFAULT_BOARD);
        final Session mockSession = mock(Session.class);
        final SimpleNaturalIdLoadAccess naturalIdLoadAccess = mock(SimpleNaturalIdLoadAccess.class);

        when(entityManager.unwrap(Session.class)).thenReturn(mockSession);
        when(mockSession.bySimpleNaturalId(Board.class)).thenReturn(naturalIdLoadAccess);
        when(naturalIdLoadAccess.getReference(DEFAULT_BOARD)).thenReturn(mockBoard);

        final Picture mockPicture = new Picture();
        mockPicture.setName(DEFAULT_PICTURE);
        doNothing().when(minioService).savePicture(mockPicture, MinioService.BUCKET);

        when(posterRepository.thread(any(Threads.class))).thenReturn(0L);

        posterService.thread(mockPost, mockPicture, DEFAULT_BOARD);

        verify(minioService).savePicture(mockPicture, MinioService.BUCKET);
        verify(posterRepository).thread(threadsCaptor.capture());
        final Threads threads = threadsCaptor.getValue();
        assertEquals(DEFAULT_CONTENT, threads.getHeader());
        assertEquals(DEFAULT_CONTENT, threads.getContent());
        assertEquals(DEFAULT_PICTURE, threads.getPicture());
        assertEquals(DEFAULT_BOARD, threads.getBoard().getName());
    }

    @Test
    public void thread_GettingPostWithoutPicture_ThrowsIllegalArgumentException() {
        final Post mockPost = new Post();
        mockPost.setBody(DEFAULT_CONTENT);

        assertThrows(IllegalArgumentException.class, () -> posterService.thread(mockPost, null, DEFAULT_BOARD));
    }

    @Test
    public void post_GettingPostWithHead_GettingPostWithPicture_Success() {
        final Post mockPost = new Post();
        mockPost.setHead(DEFAULT_HEADER);
        mockPost.setBody(DEFAULT_CONTENT);

        final Picture mockPicture = new Picture();
        mockPicture.setName(DEFAULT_PICTURE);
        doNothing().when(minioService).savePicture(mockPicture, MinioService.BUCKET);

        final long threadId = 0L;
        Threads mockThread = new Threads();
        when(posterRepository.getThreadsById(threadId)).thenReturn(mockThread);
        doNothing().when(posterRepository).post(any(Posts.class));

        posterService.post(mockPost, mockPicture, threadId);

        verify(minioService).savePicture(mockPicture, MinioService.BUCKET);
        verify(posterRepository).post(postsCaptor.capture());
        final Posts posts = postsCaptor.getValue();
        assertEquals(DEFAULT_HEADER, posts.getHeader());
        assertEquals(DEFAULT_CONTENT, posts.getContent());
        assertEquals(DEFAULT_PICTURE, posts.getPicture());
        assertEquals(mockThread, posts.getThread());
    }

    @Test
    public void post_GettingPostWithoutHead_GettingPostWithPicture_Success() {
        final Post mockPost = new Post();
        mockPost.setBody(DEFAULT_CONTENT);

        final Picture mockPicture = new Picture();
        mockPicture.setName(DEFAULT_PICTURE);
        doNothing().when(minioService).savePicture(mockPicture, MinioService.BUCKET);

        final long threadId = 0L;
        Threads mockThread = new Threads();
        when(posterRepository.getThreadsById(threadId)).thenReturn(mockThread);
        doNothing().when(posterRepository).post(any(Posts.class));

        posterService.post(mockPost, mockPicture, threadId);

        verify(minioService).savePicture(mockPicture, MinioService.BUCKET);
        verify(posterRepository).post(postsCaptor.capture());
        final Posts posts = postsCaptor.getValue();
        assertNull(posts.getHeader());
        assertEquals(DEFAULT_CONTENT, posts.getContent());
        assertEquals(DEFAULT_PICTURE, posts.getPicture());
        assertEquals(mockThread, posts.getThread());
    }

    @Test
    public void post_GettingPostWithHead_GettingPostWithoutPicture_Success() {
        final Post mockPost = new Post();
        mockPost.setHead(DEFAULT_HEADER);
        mockPost.setBody(DEFAULT_CONTENT);

        final long threadId = 0L;
        Threads mockThread = new Threads();
        when(posterRepository.getThreadsById(threadId)).thenReturn(mockThread);
        doNothing().when(posterRepository).post(any(Posts.class));

        posterService.post(mockPost, null, threadId);

        verify(posterRepository).post(postsCaptor.capture());
        final Posts posts = postsCaptor.getValue();
        assertEquals(DEFAULT_HEADER, posts.getHeader());
        assertEquals(DEFAULT_CONTENT, posts.getContent());
        assertEquals("", posts.getPicture());
        assertEquals(mockThread, posts.getThread());
    }

    @Test
    public void post_GettingPostWithoutHead_GettingPostWithoutPicture_Success() {
        final Post mockPost = new Post();
        mockPost.setBody(DEFAULT_CONTENT);

        final long threadId = 0L;
        Threads mockThread = new Threads();
        when(posterRepository.getThreadsById(threadId)).thenReturn(mockThread);
        doNothing().when(posterRepository).post(any(Posts.class));

        posterService.post(mockPost, null, threadId);

        verify(posterRepository).post(postsCaptor.capture());
        final Posts posts = postsCaptor.getValue();
        assertNull(posts.getHeader());
        assertEquals(DEFAULT_CONTENT, posts.getContent());
        assertEquals("", posts.getPicture());
        assertEquals(mockThread, posts.getThread());
    }

    @Test
    public void board_Success() {
        final String mockBoardName = DEFAULT_BOARD;
        final String mockPassword = "password";
        final String mockRule = "rwdxrwdxrwdx";
        final String mockNickname = "nickname";
        final int mockLifeCycleThreads = 14;
        final int mockLifeCyclePosts = 7;
        final String mockTranscription = "transcription";

        final String mockEncodedPassword = "encodedPassword";
        when(passwordEncoder.encode(mockPassword)).thenReturn(mockEncodedPassword);

        doNothing().when(posterRepository).board(any(Board.class), any(Users.class));

        posterService.board(mockBoardName, mockPassword, mockRule, mockNickname, mockLifeCycleThreads, mockLifeCyclePosts, mockTranscription);

        verify(passwordEncoder).encode(mockPassword);
        verify(posterRepository).board(boardCaptor.capture(), usersCaptor.capture());

        final Board board = boardCaptor.getValue();
        assertEquals(mockBoardName, board.getName());
        assertEquals(mockRule, board.getRules());
        assertEquals(mockLifeCyclePosts, board.getLifeCyclePosts());
        assertEquals(mockTranscription, board.getTranscription());
        assertEquals(mockLifeCycleThreads, board.getLifeCycleThreads());

        final Users users = usersCaptor.getValue();
        assertEquals(mockEncodedPassword, users.getPassword());
        assertEquals(mockNickname, users.getNickname());
        assertEquals(board, users.getBoard());
        assertTrue(users.isRole());
    }

    @Test
    public void board_GettingBoardWithoutTranscription_Success() {
        final String mockBoardName = DEFAULT_BOARD;
        final String mockPassword = "password";
        final String mockRule = "rwdxrwdxrwdx";
        final String mockNickname = "nickname";
        final int mockLifeCycleThreads = 14;
        final int mockLifeCyclePosts = 7;

        final String mockEncodedPassword = "encodedPassword";
        when(passwordEncoder.encode(mockPassword)).thenReturn(mockEncodedPassword);

        doNothing().when(posterRepository).board(any(Board.class), any(Users.class));

        posterService.board(mockBoardName, mockPassword, mockRule, mockNickname, mockLifeCycleThreads, mockLifeCyclePosts, null);

        verify(passwordEncoder).encode(mockPassword);
        verify(posterRepository).board(boardCaptor.capture(), usersCaptor.capture());

        final Board board = boardCaptor.getValue();
        assertEquals(mockBoardName, board.getName());
        assertEquals(mockRule, board.getRules());
        assertEquals(mockLifeCyclePosts, board.getLifeCyclePosts());
        assertEquals(mockBoardName, board.getTranscription());
        assertEquals(mockLifeCycleThreads, board.getLifeCycleThreads());

        final Users users = usersCaptor.getValue();
        assertEquals(mockEncodedPassword, users.getPassword());
        assertEquals(mockNickname, users.getNickname());
        assertEquals(board, users.getBoard());
        assertTrue(users.isRole());
    }

    @Test
    public void board_GettingLifeCircleOfPost_MoreThan_LifeCycleThreads_ThrowsInvalidRulesException() {
        final String mockBoardName = DEFAULT_BOARD;
        final String mockPassword = "password";
        final String mockRule = "rwdxrwdxrwdx";
        final String mockNickname = "nickname";
        final int mockLifeCycleThreads = 7;
        final int mockLifeCyclePosts = 14;
        final String mockTranscription = "transcription";

        assertThrows(InvalidRulesException.class, () -> {
            posterService.board(
                    mockBoardName,
                    mockPassword,
                    mockRule,
                    mockNickname,
                    mockLifeCycleThreads,
                    mockLifeCyclePosts,
                    mockTranscription);
        });
    }

    @Test
    public void board_GettingNegativeLifeCircleOfPost_ThrowsInvalidRulesException() {
        final String mockBoardName = DEFAULT_BOARD;
        final String mockPassword = "password";
        final String mockRule = "rwdxrwdxrwdx";
        final String mockNickname = "nickname";
        final int mockLifeCycleThreads = 7;
        final int mockLifeCyclePosts = -14;
        final String mockTranscription = "transcription";

        assertThrows(InvalidRulesException.class, () -> {
            posterService.board(
                    mockBoardName,
                    mockPassword,
                    mockRule,
                    mockNickname,
                    mockLifeCycleThreads,
                    mockLifeCyclePosts,
                    mockTranscription);
        });
    }


    @Test
    public void board_GettingToMuchLifeCircleOfThreads_ThrowsInvalidRulesException() {
        final String mockBoardName = DEFAULT_BOARD;
        final String mockPassword = "password";
        final String mockRule = "rwdxrwdxrwdx";
        final String mockNickname = "nickname";
        final int mockLifeCycleThreads = Board.MAX_LIFE_CYCLE_OF_THREADS + 1;
        final int mockLifeCyclePosts = 7;
        final String mockTranscription = "transcription";

        assertThrows(InvalidRulesException.class, () -> {
            posterService.board(
                    mockBoardName,
                    mockPassword,
                    mockRule,
                    mockNickname,
                    mockLifeCycleThreads,
                    mockLifeCyclePosts,
                    mockTranscription);
        });
    }

    @ParameterizedTest(name = "{0} - rule: {1}")
    @CsvSource({
            "Getting too long rules, rwdxrwdxrwdxrwdx",
            "Getting incorrect combination, rdwxrxwdrwxd",
            "Without rights to reading for owner, -wdxrwdxrwdx",
            "Without rights to reading for group, rwdx-wdxrwdx",
            "Without rights to reading for anons, rwdxrwdx-wdx",
            "Invalid characters, abcdefghijkl",
            "Random gibberish, !@#$%^&*()_+",
    })
    public void board_WithInvalidRules_ThrowsInvalidRulesException(String description, String invalidRule) {
        final String mockBoardName = DEFAULT_BOARD;
        final String mockPassword = "password";
        final String mockNickname = "nickname";
        final int mockLifeCycleThreads = 14;
        final int mockLifeCyclePosts = 7;
        final String mockTranscription = "transcription";

        assertThrows(InvalidRulesException.class, () -> {
            posterService.board(
                    mockBoardName,
                    mockPassword,
                    invalidRule,
                    mockNickname,
                    mockLifeCycleThreads,
                    mockLifeCyclePosts,
                    mockTranscription
            );
        });
    }
}
