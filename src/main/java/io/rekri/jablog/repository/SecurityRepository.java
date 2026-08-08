package io.rekri.jablog.repository;

import io.rekri.jablog.repository.security.BoardRepoSecurity;
import io.rekri.jablog.repository.security.PostRepoSecurity;
import io.rekri.jablog.repository.security.ThreadRepoSecurity;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class SecurityRepository {

    private final BoardRepoSecurity boardRepoSecurity;
    private final ThreadRepoSecurity threadRepoSecurity;
    private final PostRepoSecurity postRepo;

    @NotNull
    public String getRulesByBoardName(String boardName){
        return boardRepoSecurity.getRulesByBoardName(boardName);
    }

    public boolean isThreadInBoard(String boardName, long threadId){
        Long count = threadRepoSecurity.findThreadInBoard(threadId, boardName);

        return count > 0;
    }

    public boolean isPostInBoard(String boardName, long postId){
        Long count = postRepo.findPosInBoard(postId, boardName);

        return count > 0;
    }
}
