package com.example.jablog.service;

import com.example.jablog.DTO.Login;
import com.example.jablog.config.security.CustomUserDetails;
import com.example.jablog.entity.Users;
import com.example.jablog.repository.APIRepository;
import jakarta.persistence.NoResultException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class APIService {

    private final APIRepository apiRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public CustomUserDetails login (Login login) throws NoResultException {

        Users user;

        try {
            user = apiRepository.login(login.getNickname());
        } catch (NoResultException e){
            throw new NoResultException();
        }

        if (!passwordEncoder.matches(login.getPassword(), user.getPassword()))
            throw new NoResultException();

        return CustomUserDetails.build(user);
    }

}
