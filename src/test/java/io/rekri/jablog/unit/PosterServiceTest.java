package io.rekri.jablog.unit;

import io.rekri.jablog.DTO.BoardToCreate;
import io.rekri.jablog.DTO.Picture;
import io.rekri.jablog.DTO.Post;
import io.rekri.jablog.entity.Board;
import io.rekri.jablog.errors.InvalidRulesException;
import io.rekri.jablog.repository.PosterRepository;
import io.rekri.jablog.service.MinioService;
import io.rekri.jablog.service.PosterService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
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
    private MinioService minioService;
    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private PosterService posterService;

    final private static String DEFAULT_HEADER = "head_default";
    final private static String DEFAULT_CONTENT = "content_default";
    final private static String DEFAULT_PICTURE = "picture_default";
    final private static String DEFAULT_BOARD = "board_default";
    final private static String DEFAULT_ACCOUNT = "account_default";

    @Test
    public void thread_GettingPostWithHead_Success() {
        final Post mockPost = new Post();
        mockPost.setHead(DEFAULT_HEADER);
        mockPost.setBody(DEFAULT_CONTENT);

        final Picture mockPicture = new Picture();
        mockPicture.setName(DEFAULT_PICTURE);

        when(posterRepository.thread(DEFAULT_CONTENT, DEFAULT_HEADER, DEFAULT_PICTURE, DEFAULT_BOARD)).thenReturn(1L);
        doNothing().when(minioService).savePicture(mockPicture, MinioService.BUCKET);

        long result = posterService.thread(mockPost, mockPicture, DEFAULT_BOARD);

        assertEquals(1L, result);
        verify(posterRepository).thread(DEFAULT_CONTENT, DEFAULT_HEADER, DEFAULT_PICTURE, DEFAULT_BOARD);
        verify(minioService).savePicture(mockPicture, MinioService.BUCKET);
    }

    @Test
    public void thread_GettingPostWithoutHead_Success() {
        final Post mockPost = new Post();
        mockPost.setBody(DEFAULT_CONTENT);

        final Picture mockPicture = new Picture();
        mockPicture.setName(DEFAULT_PICTURE);

        when(posterRepository.thread(DEFAULT_CONTENT, DEFAULT_CONTENT, DEFAULT_PICTURE, DEFAULT_BOARD)).thenReturn(1L);
        doNothing().when(minioService).savePicture(mockPicture, MinioService.BUCKET);

        long result = posterService.thread(mockPost, mockPicture, DEFAULT_BOARD);

        assertEquals(1L, result);
        verify(posterRepository).thread(DEFAULT_CONTENT, DEFAULT_CONTENT, DEFAULT_PICTURE, DEFAULT_BOARD);
        verify(minioService).savePicture(mockPicture, MinioService.BUCKET);
    }

    @Test
    public void thread_GettingPostWithoutPicture_ThrowsNullPointerException() {
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

        final long threadId = 1L;

        posterService.post(mockPost, mockPicture, threadId);

        verify(posterRepository).post(DEFAULT_CONTENT, DEFAULT_HEADER, DEFAULT_PICTURE, threadId);
        verify(minioService).savePicture(mockPicture, MinioService.BUCKET);
    }

    @Test
    public void post_GettingPostWithoutHead_GettingPostWithPicture_Success() {
        final Post mockPost = new Post();
        mockPost.setBody(DEFAULT_CONTENT);

        final Picture mockPicture = new Picture();
        mockPicture.setName(DEFAULT_PICTURE);

        final long threadId = 1L;

        posterService.post(mockPost, mockPicture, threadId);

        verify(posterRepository).post(DEFAULT_CONTENT, null, DEFAULT_PICTURE, threadId);
        verify(minioService).savePicture(mockPicture, MinioService.BUCKET);
    }

    @Test
    public void post_GettingPostWithHead_GettingPostWithoutPicture_Success() {
        final Post mockPost = new Post();
        mockPost.setHead(DEFAULT_HEADER);
        mockPost.setBody(DEFAULT_CONTENT);

        final long threadId = 1L;

        posterService.post(mockPost, null, threadId);

        verify(posterRepository).post(DEFAULT_CONTENT, DEFAULT_HEADER, "", threadId);
        verify(minioService, never()).savePicture(any(), any());
    }

    @Test
    public void post_GettingPostWithoutHead_GettingPostWithoutPicture_Success() {
        final Post mockPost = new Post();
        mockPost.setBody(DEFAULT_CONTENT);

        final long threadId = 1L;

        posterService.post(mockPost, null, threadId);

        verify(posterRepository).post(DEFAULT_CONTENT, null, "", threadId);
        verify(minioService, never()).savePicture(any(), any());
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

        BoardToCreate boardToCreate = new BoardToCreate(mockBoardName,
                mockRule,
                mockPassword,
                mockNickname,
                mockLifeCycleThreads,
                mockLifeCyclePosts,
                mockTranscription);

        posterService.board(boardToCreate, DEFAULT_ACCOUNT);

        verify(passwordEncoder).encode(mockPassword);
        verify(posterRepository).board(boardToCreate, mockEncodedPassword, DEFAULT_ACCOUNT);
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

        BoardToCreate boardToCreate = new BoardToCreate(mockBoardName,
                mockRule,
                mockPassword,
                mockNickname,
                mockLifeCycleThreads,
                mockLifeCyclePosts,
                null);

        posterService.board(boardToCreate, DEFAULT_ACCOUNT);

        verify(passwordEncoder).encode(mockPassword);
        assertEquals(mockBoardName, boardToCreate.getTranscription());
        verify(posterRepository).board(boardToCreate, mockEncodedPassword, DEFAULT_ACCOUNT);
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

        BoardToCreate boardToCreate = new BoardToCreate(mockBoardName,
                mockRule,
                mockPassword,
                mockNickname,
                mockLifeCycleThreads,
                mockLifeCyclePosts,
                mockTranscription);

        assertThrows(InvalidRulesException.class, () -> posterService.board(boardToCreate, DEFAULT_ACCOUNT));
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

        BoardToCreate boardToCreate = new BoardToCreate(mockBoardName,
                mockRule,
                mockPassword,
                mockNickname,
                mockLifeCycleThreads,
                mockLifeCyclePosts,
                mockTranscription);

        assertThrows(InvalidRulesException.class, () -> posterService.board(boardToCreate, DEFAULT_ACCOUNT));
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

        BoardToCreate boardToCreate = new BoardToCreate(mockBoardName,
                mockRule,
                mockPassword,
                mockNickname,
                mockLifeCycleThreads,
                mockLifeCyclePosts,
                mockTranscription);

        assertThrows(InvalidRulesException.class, () -> posterService.board(boardToCreate, DEFAULT_ACCOUNT));
    }

    @ParameterizedTest(name = "{0} - rule: {1}")
    @CsvSource({
            "Getting too long rules, rwdxrwdxrwdxrwdx",
            "Getting incorrect combination, rdwxrxwdrwxd",
            "Without rights to reading for owner, -wdxrwdxrwdx",
            "Without rights to reading for group, rwdx-wdxrwdx",
            "Without rights to reading for anons, rwdxrwdx-wdx",
            "Invalid characters, abcdefghijkl",
            "Random gibberish, !@#$%^&*()_+"
    })
    public void board_WithInvalidRules_ThrowsInvalidRulesException(String description, String invalidRule) {
        final String mockBoardName = DEFAULT_BOARD;
        final String mockPassword = "password";
        final String mockNickname = "nickname";
        final int mockLifeCycleThreads = 14;
        final int mockLifeCyclePosts = 7;
        final String mockTranscription = "transcription";

        BoardToCreate boardToCreate = new BoardToCreate(mockBoardName,
                invalidRule,
                mockPassword,
                mockNickname,
                mockLifeCycleThreads,
                mockLifeCyclePosts,
                mockTranscription);

        assertThrows(InvalidRulesException.class, () -> posterService.board(boardToCreate, DEFAULT_ACCOUNT));
    }
}