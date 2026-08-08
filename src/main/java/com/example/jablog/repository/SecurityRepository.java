package com.example.jablog.repository;

import com.example.jablog.repository.security.BoardRepo;
import com.example.jablog.repository.security.PostRepo;
import com.example.jablog.repository.security.ThreadRepo;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class SecurityRepository {

    private final BoardRepo boardRepo;
    private final ThreadRepo threadRepo;
    private final PostRepo postRepo;

    @NotNull
    public String getRulesByBoardName(String boardName){
        return boardRepo.getRulesByBoardName(boardName);
    }

    public boolean isThreadInBoard(String boardName, long threadId){
        Long count = threadRepo.findThreadInBoard(threadId, boardName);

        return count > 0;
    }

    public boolean isPostInBoard(String boardName, long postId){
        Long count = postRepo.findPosInBoard(postId, boardName);

        return count > 0;
    }
}
