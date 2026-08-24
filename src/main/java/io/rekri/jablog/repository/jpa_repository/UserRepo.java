package io.rekri.jablog.repository.jpa_repository;

import io.rekri.jablog.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepo extends JpaRepository<Users,Long> {
    Optional<Users> findByNickname(String nickname);

    @Modifying
    @Query("delete from Users u where u.nickname = :nickname and u.board.name = :boardName")
    void deleteByUserNameAndBoardName(@Param("nickname") String userName, @Param("boardName") String boardName);

    @Query("select u.nickname from Users u where u.board.name = :boardName")
    List<String> findAllUserNamesByBoardName(@Param("boardName") String boardName);

    @Query("""
        select u
        from Users u
        where (
            u.board = :boardName
            and
            exists (
            select 1 from Records r where r.account.username = :accountName and r.user = u
            )
        )
""")
    Optional<Users> findByAccountNameAndBoard(@Param("boardName") String boardName, @Param("accountName") String accountName);
}
