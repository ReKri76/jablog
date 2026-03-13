package com.example.jablog.service;

import com.example.jablog.controllers.Poster;
import com.example.jablog.repository.PosterRepository;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.List;

@Service
public class PosterService {

    @Autowired
    PosterRepository posterRepository;

    String board;
    MultipartFile file;

    @Entity
    @Table(name="posts")
    @Getter
    @Setter
    public static class Posts{
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private long id;
        @Column(name="header")

        private String header;
        @Column(name="body")
        private String body;

        @Column(name="picture")
        private String picture;

        @ManyToOne
        @JoinColumn(name="threadId")
        private Thread thread;

        @Column(name="createdAt")
        private long createdAt;
    }

    @Entity
    @Table(name="threads")
    @Getter
    @Setter
    public static class Threads{
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private long id;
        @Column(name="header")

        private String header;
        @Column(name="body")
        private String body;

        @Column(name="picture")
        private String picture;

        @OneToMany(mappedBy = "thread")
        private List<Posts> posts;

        @Column(name="createdAt")
        private long createdAt;
    }

    public int thread(Poster.@NonNull Post post, MultipartFile file, String board){

        if (post.getHead().isEmpty())
            post.setHead(post.getBody().substring(0,126));

        this.board=board;
        this.file=file;
        Threads threads = new Threads();
        threads.body=post.getBody();
        threads.header=post.getHead();
        threads.picture="placeholder";
        threads.board=board;
        threads.createdAt= new Date().getTime();

        int idOfThread = posterRepository.thread(threads, file);

        return idOfThread;
    }
}
