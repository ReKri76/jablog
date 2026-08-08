package com.example.jablog.repository;

import com.example.jablog.entity.Board;
import com.example.jablog.entity.Threads;
import com.example.jablog.repository.getter.BoardRepo;
import com.example.jablog.repository.getter.ThreadRepo;
import jakarta.persistence.NoResultException;
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

    private final BoardRepo boardRepo;
    private final ThreadRepo threadRepo;

    @NotNull
    public List<Board> start(){
        return boardRepo.findAll();
    }

   @NotNull
   public List<Threads> board(String boardName, int page){
        return threadRepo.findThreadsByBoardNameByPageable(boardName, PageRequest.of(
                page*LIMIT_OF_PAGINATION, LIMIT_OF_PAGINATION, Sort.by("id").descending()
        ));
   }

   @NotNull
   public Threads thread(long threadId){
        return threadRepo.findThreadsByIdWithPosts(threadId).orElseThrow(NoResultException::new);
    }
}
