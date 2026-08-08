package io.rekri.jablog.repository;

import io.rekri.jablog.entity.Board;
import io.rekri.jablog.entity.Threads;
import io.rekri.jablog.repository.getter.BoardRepoGetter;
import io.rekri.jablog.repository.getter.ThreadRepoGetter;
import jakarta.persistence.NoResultException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class GetterRepository {

    private static final int LIMIT_OF_PAGINATION = 10;

    private final BoardRepoGetter boardRepoGetter;
    private final ThreadRepoGetter threadRepoGetter;

    @NotNull
    @Transactional
    public List<Board> start(){
        return boardRepoGetter.findAll();
    }

   @NotNull
   @Transactional
   public List<Threads> board(String boardName, int page){
        return threadRepoGetter.findThreadsByBoardNameByPageable(boardName, PageRequest.of(
                page*LIMIT_OF_PAGINATION, LIMIT_OF_PAGINATION, Sort.by("id").ascending()
        ));
   }

   @NotNull
   @Transactional
   public Threads thread(long threadId){
        return threadRepoGetter.findThreadsByIdWithPosts(threadId).orElseThrow(NoResultException::new);
    }
}
