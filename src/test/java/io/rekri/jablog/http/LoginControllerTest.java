package io.rekri.jablog.http;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.rekri.jablog.config.security.CustomUserDetails;
import io.rekri.jablog.controllers.LoginController;
import io.rekri.jablog.repository.UserDetailsRepository;
import io.rekri.jablog.service.CustomUserDetailsService;
import io.rekri.jablog.service.LoginService;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class LoginControllerTest {

    @Mock
    private LoginService loginService;

    @InjectMocks
    private LoginController loginController;

    private MockMvc mockMvc;

    UserDetailsRepository  userDetailsRepository = Mockito.mock(UserDetailsRepository.class);
    private final CustomUserDetailsService customUserDetailsService = new CustomUserDetailsService(userDetailsRepository);

    private final String DEFAULT_LOGIN = "default_login";
    private final String DEFAULT_PASSWORD = "default_password";

    @Captor
    private ArgumentCaptor<io.rekri.jablog.DTO.Login> loginCaptor;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(loginController).build();
    }

    @Test
    public void login_Success() throws Exception {
        final CustomUserDetails mockCustomUserDetails = customUserDetailsService.createDefault();

        when(loginService.login(any(io.rekri.jablog.DTO.Login.class))).thenReturn(mockCustomUserDetails);

        io.rekri.jablog.DTO.Login req = new io.rekri.jablog.DTO.Login();
        req.setNickname(DEFAULT_LOGIN);
        req.setPassword(DEFAULT_PASSWORD);

        mockMvc.perform(post("/api/login/verify")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(req))
                )
                .andExpect(status().isOk())
                .andExpect(result -> {
                    HttpSession session = result.getRequest().getSession(false);
                    assertEquals(mockCustomUserDetails, session.getAttribute(mockCustomUserDetails.getBoardName()));
                });

        verify(loginService).login(loginCaptor.capture());
        io.rekri.jablog.DTO.Login login = loginCaptor.getValue();
        assertEquals(DEFAULT_LOGIN,  login.getNickname());
        assertEquals(DEFAULT_PASSWORD, login.getPassword());
    }
}
