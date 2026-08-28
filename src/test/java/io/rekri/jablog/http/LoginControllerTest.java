package io.rekri.jablog.http;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.rekri.jablog.DTO.Login;
import io.rekri.jablog.DTO.Tokens;
import io.rekri.jablog.controllers.LoginController;
import io.rekri.jablog.service.LoginService;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class LoginControllerTest {

    @Mock
    private LoginService loginService;

    @InjectMocks
    private LoginController loginController;

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final String DEFAULT_NICKNAME = "default_login";
    private static final String DEFAULT_PASSWORD = "default_password";
    private static final String DEFAULT_ACCOUNT_NAME = "board1";

    @Captor
    private ArgumentCaptor<Login> loginCaptor;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(loginController).build();
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void login_WithAuthentication_ReturnsOk() throws Exception {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(DEFAULT_ACCOUNT_NAME, null)
        );

        Login req = new Login();
        req.setNickname(DEFAULT_NICKNAME);
        req.setPassword(DEFAULT_PASSWORD);

        mockMvc.perform(post("/api/login/extend-accont")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk());

        verify(loginService).login(loginCaptor.capture(), eq(DEFAULT_ACCOUNT_NAME));
        Login captured = loginCaptor.getValue();
        assertEquals(DEFAULT_NICKNAME, captured.getNickname());
        assertEquals(DEFAULT_PASSWORD, captured.getPassword());
    }

    @Test
    void login_WithoutAuthentication_Throws() throws Exception {
        SecurityContextHolder.clearContext();

        Login req = new Login();
        req.setNickname(DEFAULT_NICKNAME);
        req.setPassword(DEFAULT_PASSWORD);

        mockMvc.perform(post("/api/login/extend-accont")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void createAccount_Success() throws Exception {
        Login req = new Login();
        req.setNickname("newUser");
        req.setPassword("rawPassword");

        Tokens tokens = new Tokens("access-token", "refresh-token");
        when(loginService.createAccount(any(Login.class))).thenReturn(tokens);

        mockMvc.perform(post("/api/login/create-account")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(result -> {
                    Cookie cookie = result.getResponse().getCookie("refreshToken");
                    assertNotNull(cookie, "refreshToken cookie should be set");
                    assertEquals("refresh-token", cookie.getValue());
                    assertTrue(cookie.isHttpOnly());
                })
                .andExpect(result -> {
                    String body = result.getResponse().getContentAsString();
                    assertTrue(body.contains("access-token"));
                    assertTrue(body.contains("\"status\":201"));
                });

        verify(loginService).createAccount(loginCaptor.capture());
        assertEquals("newUser", loginCaptor.getValue().getNickname());
    }

    @Test
    void refresh_WithValidCookie_ReturnsOk() throws Exception {
        Tokens tokens = new Tokens("new-access-token", "new-refresh-token");
        when(loginService.refresh("old-refresh-token")).thenReturn(tokens);

        mockMvc.perform(post("/api/login/refresh")
                        .cookie(new Cookie("refreshToken", "old-refresh-token")))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    Cookie cookie = result.getResponse().getCookie("refreshToken");
                    assertNotNull(cookie);
                    assertEquals("new-refresh-token", cookie.getValue());
                })
                .andExpect(result -> {
                    String body = result.getResponse().getContentAsString();
                    assertTrue(body.contains("new-access-token"));
                });

        verify(loginService).refresh("old-refresh-token");
    }

    @Test
    void refresh_WithoutCookie_Throws() throws Exception {
        mockMvc.perform(post("/api/login/refresh"))
                .andExpect(status().isUnauthorized());
    }
}
