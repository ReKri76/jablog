package io.rekri.jablog.repository.jpa_repository;

import io.rekri.jablog.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepo extends JpaRepository<Users,Long> {
    Optional<Users> findByNickname(String nickname);

    @Query("delete from Users u where u.nickname = :nickname and u.board.name = :boardName")
    void deleteByUserNameAndBoardName(@Param("nickname") String userName, @Param("boardName") String boardName);

    @Query("select u.nickname from Users u where u.board.name = :boardName")
    List<String> findAllUserNamesByBoardName(@Param("boardName") String boardName);
}
