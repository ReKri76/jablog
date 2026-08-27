package io.rekri.jablog.repository;

import io.rekri.jablog.DTO.Login;
import io.rekri.jablog.entity.Accounts;
import io.rekri.jablog.entity.Records;
import io.rekri.jablog.entity.Users;
import io.rekri.jablog.repository.jpa_repository.AccountRepo;
import io.rekri.jablog.repository.jpa_repository.RecordRepo;
import io.rekri.jablog.repository.jpa_repository.UserRepo;
import jakarta.persistence.NoResultException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class LoginRepository {

    private final UserRepo userRepo;
    private final AccountRepo accountRepo;
    private final RecordRepo recordRepo;

    @Transactional
    public Users login(@NotNull String nickname) throws NoResultException {
        return userRepo.findByNickname(nickname)
                .orElseThrow(NoResultException::new);
    }

    @Transactional
    public void extendAccount(@NotNull Users users, @NotNull String accountName){
        Accounts accounts = accountRepo.getReferenceByUsername(accountName);

        Records records = new Records();
        records.setAccount(accounts);
        records.setUser(users);
        recordRepo.save(records);
    }

    @Transactional
    public void createAccount(@NotNull Login login){
        Accounts accounts = new Accounts();
        accounts.setPassword(login.getPassword());
        accounts.setUsername(login.getNickname());
        accountRepo.save(accounts);
    }

    @Transactional
    public boolean isAccountNameAlreadyUsed(@NotNull String accountName){
        Optional<Accounts> accounts = accountRepo.findByUsername(accountName);
        return accounts.isPresent();
    }

    @Transactional
    public Optional<Accounts> findAccountByUsername(@NotNull String username){
        return accountRepo.findByUsername(username);
    }

    @Transactional
    public void updateRefreshExpiredTime(@NotNull Accounts account, long newExpiredTime){
        account.setRefreshExpiredTime(newExpiredTime);
        accountRepo.save(account);
    }
}