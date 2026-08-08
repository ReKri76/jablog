package io.rekri.jablog.http;

import io.rekri.jablog.DTO.Login;
import io.rekri.jablog.config.security.CustomUserDetails;
import io.rekri.jablog.controllers.API;
import io.rekri.jablog.repository.UserDetailsRepository;
import io.rekri.jablog.service.APIService;
import io.rekri.jablog.service.CustomUserDetailsService;
import jakarta.persistence.NoResultException;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class APITest {

    @Mock
    private APIService apiService;

    @InjectMocks
    private API api;

    private MockMvc mockMvc;

    UserDetailsRepository  userDetailsRepository = Mockito.mock(UserDetailsRepository.class);
    private CustomUserDetailsService customUserDetailsService = new CustomUserDetailsService(userDetailsRepository);

    private final String DEFAULT_LOGIN = "default_login";
    private final String DEFAULT_PASSWORD = "default_password";

    @Captor
    private ArgumentCaptor<Login> loginCaptor;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(api).build();
    }

    @Test
    public void login_Success() throws Exception {
        final CustomUserDetails mockCustomUserDetails = customUserDetailsService.createDefault();

        when(apiService.login(any(Login.class))).thenReturn(mockCustomUserDetails);

        mockMvc.perform(post("/api/login/verify")
                .param("nickname", DEFAULT_LOGIN)
                .param("password", DEFAULT_PASSWORD)
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/" + mockCustomUserDetails.getBoardName()))
                .andExpect(result -> {
                    HttpSession session = result.getRequest().getSession(false);
                    assertEquals(mockCustomUserDetails, session.getAttribute(mockCustomUserDetails.getBoardName()));
                });

        verify(apiService).login(loginCaptor.capture());
        Login login = loginCaptor.getValue();
        assertEquals(DEFAULT_LOGIN,  login.getNickname());
        assertEquals(DEFAULT_PASSWORD, login.getPassword());
    }

    @Test
    public void login_Fail() throws Exception {
        when(apiService.login(any(Login.class))).thenThrow(NoResultException.class);

        mockMvc.perform(post("/api/login/verify")
                .param("nickname", DEFAULT_LOGIN)
                .param("password", DEFAULT_PASSWORD)
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/api/login")
                );
        verify(apiService).login(any(Login.class));
    }
}
