package io.rekri.jablog.repository.users;

import io.rekri.jablog.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserRepoUsers extends JpaRepository<Users, Long> {
    @Query("delete from Users u where u.nickname = :nickname and u.board.name = :boardName")
    void deleteByUserNameAndBoardName(@Param("nickname") String userName, @Param("boardName") String boardName);

    @Query("select u.nickname from Users u where u.board.name = :boardName")
    List<String> findAllUserNamesByBoardName(@Param("boardName") String boardName);
}
