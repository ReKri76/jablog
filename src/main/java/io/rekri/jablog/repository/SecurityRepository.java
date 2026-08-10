package io.rekri.jablog.repository;

import io.rekri.jablog.repository.jpa_repository.BoardRepo;
import io.rekri.jablog.repository.jpa_repository.PostRepo;
import io.rekri.jablog.repository.jpa_repository.ThreadRepo;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class SecurityRepository {

    private final BoardRepo boardRepoSecurity;
    private final ThreadRepo threadRepoSecurity;
    private final PostRepo postRepo;

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
