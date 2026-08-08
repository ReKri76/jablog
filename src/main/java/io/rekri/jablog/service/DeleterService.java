package io.rekri.jablog.service;

import io.rekri.jablog.entity.Posts;
import io.rekri.jablog.entity.Threads;
import io.rekri.jablog.repository.DeleterRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class DeleterService {

    private final DeleterRepository deleterRepository;
    private final MinioService minioService;

    @Transactional
    public void thread(long threadId){
        log.info("Thread number {} is start deleting.",threadId);
        Threads threads = deleterRepository.thread(threadId);
        minioService.deletePicture(threads.getPicture(), MinioService.BUCKET);
        log.info("Thread number {} is deleted.",threadId);
    }

    @Transactional
    public void post(long postId){
        log.info("Post number {} is start deleting.",postId);
        Posts posts = deleterRepository.post(postId);
        String pic = posts.getPicture();
        if (pic!=null && !pic.isEmpty())
            minioService.deletePicture(pic, MinioService.BUCKET);
        log.info("Post number {} is deleted.",postId);
    }
}
