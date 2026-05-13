package com.example.jablog.controllers;

import com.example.jablog.DTO.Picture;
import com.example.jablog.DTO.Post;
import com.example.jablog.service.PosterService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class PosterMultipartPostTest {

    private MockMvc mockMvc;
    private PosterService posterService;

    @BeforeEach
    void setUp() {
        posterService = mock(PosterService.class);
        final Poster posterController = new Poster(posterService);

        final LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();

        mockMvc = MockMvcBuilders.standaloneSetup(posterController)
                .setMessageConverters(new MappingJackson2HttpMessageConverter())
                .setValidator(validator)
                .build();
    }

    @Test
    void multipartPostWithImageShouldRedirectBackToThread() throws Exception {
        doNothing().when(posterService).post(any(Post.class), any(Picture.class), eq(777L));

        final MockMultipartFile imagePart = new MockMultipartFile(
                "image",
                "cat.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                "fake-jpeg-content".getBytes()
        );

        mockMvc.perform(multipart("/poster/{boardName}/{threadID}", "b", 777L)
                        .file(imagePart)
                        .param("head", "hello")
                        .param("body", "world")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/b/777"));

        final ArgumentCaptor<Post> postCaptor = ArgumentCaptor.forClass(Post.class);
        verify(posterService).post(postCaptor.capture(), any(Picture.class), eq(777L));
        assertThat(postCaptor.getValue().getHead()).isEqualTo("hello");
        assertThat(postCaptor.getValue().getBody()).isEqualTo("world");
    }

    @Test
    void multipartPostWithNonImageShouldReturnBadRequest() throws Exception {
        final MockMultipartFile imagePart = new MockMultipartFile(
                "image",
                "note.txt",
                MediaType.TEXT_PLAIN_VALUE,
                "not-an-image".getBytes()
        );

        mockMvc.perform(multipart("/poster/{boardName}/{threadID}", "b", 777L)
                        .file(imagePart)
                        .param("head", "hello")
                        .param("body", "world")
                        .with(csrf()))
                .andExpect(status().isBadRequest());

        verify(posterService, never()).post(any(Post.class), any(Picture.class), eq(777L));
    }

    @Test
    void multipartPostWithoutImageShouldPassNullPicture() throws Exception {
        doNothing().when(posterService).post(any(Post.class), eq(null), eq(777L));

        mockMvc.perform(multipart("/poster/{boardName}/{threadID}", "b", 777L)
                        .param("head", "hello")
                        .param("body", "world")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/b/777"));

        verify(posterService).post(any(Post.class), eq(null), eq(777L));
    }

    @Test
    void multipartThreadWithImageShouldRedirectToCreatedThread() throws Exception {
        when(posterService.thread(any(Post.class), any(Picture.class), eq("b")))
                .thenReturn(123L);

        final MockMultipartFile imagePart = new MockMultipartFile(
                "image",
                "cat.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                "fake-jpeg-content".getBytes()
        );

        mockMvc.perform(multipart("/poster/{boardName}", "b")
                        .file(imagePart)
                        .param("head", "hello")
                        .param("body", "world")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/b/123"));

        verify(posterService).thread(any(Post.class), any(Picture.class), eq("b"));
    }

    @Test
    void multipartThreadWithEmptyImageShouldReturnBadRequest() throws Exception {
        final MockMultipartFile imagePart = new MockMultipartFile(
                "image",
                "",
                MediaType.IMAGE_JPEG_VALUE,
                new byte[0]
        );

        mockMvc.perform(multipart("/poster/{boardName}", "b")
                        .file(imagePart)
                        .param("head", "hello")
                        .param("body", "world")
                        .with(csrf()))
                .andExpect(status().isBadRequest());

        verify(posterService, never()).thread(any(Post.class), any(Picture.class), eq("b"));
    }

    @Test
    void boardCreationShouldRedirectToBoardPage() throws Exception {
        doNothing().when(posterService).board("b", "pass", "rwdxrwdxrwdx", "mod", 100, 10);

        mockMvc.perform(post("/poster/board")
                        .with(csrf())
                        .param("boardName", "b")
                        .param("rule", "rwdxrwdxrwdx")
                        .param("pass", "pass")
                        .param("nickname", "mod")
                        .param("lifeCycleThreads", "100")
                        .param("lifeCyclePosts", "10"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/b"));

        verify(posterService).board("b", "pass", "rwdxrwdxrwdx", "mod", 100, 10);
    }
}
