package com.example.jablog.service;

import com.example.jablog.DTO.PostWithPicture;
import com.example.jablog.config.security.CustomUserDetails;
import com.example.jablog.entity.Board;
import com.example.jablog.entity.Posts;
import com.example.jablog.entity.Threads;
import com.example.jablog.repository.GetterRepository;
import com.example.jablog.service.security.DeleterAccessService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;
import org.springframework.web.util.HtmlUtils;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.TreeSet;

@Service
@RequiredArgsConstructor
public class GetterService {

    private final GetterRepository getterRepository;
    private final DeleterAccessService deleterAccessService;
    private final CustomUserDetailsService customUserDetailsService;
    private final MinioService minioService;

    @Transactional
    @NotNull
    public List<com.example.jablog.DTO.Board> start(){

        final ArrayList<com.example.jablog.DTO.Board> boards = new ArrayList<>();
        final ArrayList<Board> input = getterRepository.start();

        input.forEach(board -> {
            var dto = new com.example.jablog.DTO.Board();
            dto.setTranscription(board.getTranscription());
            dto.setId(board.getName());
            boards.add(dto);
        });

        return boards;
    }

    @Transactional
    @NotNull
    public ArrayList<PostWithPicture> board(@NotNull String boardName, int page){

        final ArrayList<PostWithPicture> threads = new ArrayList<PostWithPicture>();
        final ArrayList<Threads> input = getterRepository.board(boardName, page);

        input.forEach(thread -> {

            final PostWithPicture postWithPicture = new PostWithPicture();
            postWithPicture.setId(thread.getId());
            postWithPicture.setUrl(minioService.buildPictureUrl(thread.getPicture(), boardName));
            postWithPicture.setHead(thread.getHeader());
            postWithPicture.setBody(thread.getContent());

            threads.add(postWithPicture);
        });

        return threads;
    }

    @Transactional
    @NotNull
    public ArrayList<PostWithPicture> thread(long threadId){

        final Threads threads = getterRepository.thread(threadId);
        final String boardName = threads.getBoard().getName();

        final PostWithPicture main = new PostWithPicture();
        main.setId(threads.getId());
        main.setUrl(minioService.buildPictureUrl(threads.getPicture(), boardName));
        main.setHead(threads.getHeader());
        main.setBody(threads.getContent());

        final ArrayList<PostWithPicture> posts = new ArrayList<PostWithPicture>();
        posts.add(main);

        final TreeSet<Posts> input = new TreeSet<Posts>(threads.getPosts());
        input.forEach(post -> {

            final PostWithPicture postWithPicture = new PostWithPicture();
            postWithPicture.setId(post.getId());
            String pic = post.getPicture();
            postWithPicture.setUrl(!Objects.equals(pic, "") ? minioService.buildPictureUrl(pic, boardName) : null);
            postWithPicture.setHead(post.getHeader());
            postWithPicture.setBody(createAnchor(post.getContent()));

            posts.add(postWithPicture);
        });

        return posts;
    }

    @Transactional
    public boolean canDelete(@NotNull String boardName, @Nullable CustomUserDetails customUserDetails, @Nullable String id){

        if (customUserDetails == null)
            customUserDetails = customUserDetailsService.createDefault();

        return deleterAccessService.canAccess(new SecurityData.Deleter(boardName, customUserDetails, id));
    }

    @NotNull
    public StreamingResponseBody file(@NotNull String filename){
        InputStream file = minioService.getFile(filename);

        return file::transferTo;
    }

    private String createAnchor(String text){//ссылки на другой пост
        text = HtmlUtils.htmlEscape(text);

        text = text.replaceAll("&gt;&gt;(\\d+)", "<a class=\"anchor\" href=\"#p$1\">&gt;&gt;$1</a>");

        return text;
    }
}
