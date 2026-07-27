package com.example.jablog.service;

import com.example.jablog.DTO.Login;
import com.example.jablog.DTO.PostWithPicture;
import com.example.jablog.config.security.CustomUserDetails;
import com.example.jablog.entity.Threads;
import com.example.jablog.entity.Users;
import com.example.jablog.repository.APIRepository;
import jakarta.persistence.NoResultException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class APIService {

    private final APIRepository apiRepository;
    private final PasswordEncoder passwordEncoder;
    private final CustomUserDetailsService customUserDetailsService;

    @NotNull
    public CustomUserDetails login (@NotNull Login login) throws NoResultException {

        log.info("User {} is starting login", login.getNickname());

        Users user;

        try {
            user = apiRepository.login(login.getNickname());
        } catch (NoResultException e){
            log.warn("User {} not found. Error: {}", login.getNickname(), e.getMessage());
            throw e;
        }

        if (!passwordEncoder.matches(login.getPassword(), user.getPassword())) {
            log.warn("User {} password does not match password.", login.getNickname());
            throw new NoResultException();
        }

        log.info("User {} is log in", login.getNickname());

        return customUserDetailsService.build(user);
    }

    public PostWithPicture likeThread(int threadId){
        Threads threads = apiRepository.likeThread(threadId);
        log.info("Thread {} was liked", threadId);

        PostWithPicture res = new PostWithPicture();
        res.setKarma(threads.getCarma());
        res.setId(threads.getId());
        res.setUrl(threads.getPicture());
        res.setBody(threads.getContent());
        res.setHead(threads.getHeader());
        return res;
    }

    public PostWithPicture dislikeThread(int threadId){
        Threads threads = apiRepository.dislikeThread(threadId);
        log.info("Thread {} was disliked", threadId);

        PostWithPicture res = new PostWithPicture();
        res.setKarma(threads.getCarma());
        res.setId(threads.getId());
        res.setUrl(threads.getPicture());
        res.setBody(threads.getContent());
        res.setHead(threads.getHeader());
        return res;
    }
}
