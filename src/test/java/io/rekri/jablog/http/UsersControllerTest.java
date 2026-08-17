package io.rekri.jablog.http;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.rekri.jablog.DTO.Login;
import io.rekri.jablog.config.security.CustomUserDetails;
import io.rekri.jablog.config.security.Roles;
import io.rekri.jablog.controllers.UsersController;
import io.rekri.jablog.repository.UserDetailsRepository;
import io.rekri.jablog.service.CustomUserDetailsService;
import io.rekri.jablog.service.UsersService;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class UsersControllerTest {

    @Mock
    private UsersService usersService;

    @InjectMocks
    private UsersController usersController;

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    private final UserDetailsRepository userDetailsRepository = mock(UserDetailsRepository.class);
    private final CustomUserDetailsService customUserDetails = new CustomUserDetailsService(userDetailsRepository);

    private static final String DEFAULT_NICKNAME = "nickname";
    private static final String DEFAULT_PASSWORD = "password";

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(usersController).build();
    }

    @Test
    public void addUser_Test() throws Exception {
        doNothing().when(usersService).addUser(anyString(), any(Login.class));

        Login login = new Login();
        login.setNickname(DEFAULT_NICKNAME);
        login.setPassword(DEFAULT_PASSWORD);

        mockMvc.perform(post("/api/users/panel/b/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(login)))
                .andExpect(status().isOk());

        ArgumentCaptor<String> boardNameCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Login> loginCaptor = ArgumentCaptor.forClass(Login.class);
        verify(usersService).addUser(boardNameCaptor.capture(), loginCaptor.capture());

        assertEquals("b", boardNameCaptor.getValue());
        assertEquals(DEFAULT_NICKNAME, loginCaptor.getValue().getNickname());
        assertEquals(DEFAULT_PASSWORD, loginCaptor.getValue().getPassword());
    }

    @Test
    public void deleteUser_Test() throws Exception {
        final String boardName = "b";
        final String nickname = "nickname";
        doNothing().when(usersService).deleteUser(nickname, boardName);

        mockMvc.perform(delete("/api/users/panel/" + boardName + "/" + nickname))
                .andExpect(status().isOk());

        verify(usersService).deleteUser(nickname, boardName);
    }

    @Test
    public void viewUsers_Test() throws Exception {
        final ArrayList<String> users = new ArrayList<>();
        users.add("n1");
        users.add("n2");
        users.add("n3");
        users.add("n4");
        when(usersService.viewUsers("b")).thenReturn(users);

        mockMvc.perform(get("/api/users/panel/b"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("ok"))
                .andExpect(jsonPath("$.boardName").value("b"))
                .andExpect(jsonPath("$.users", hasSize(4)))
                .andExpect(jsonPath("$.users", containsInAnyOrder("n1", "n2", "n3", "n4")));

        verify(usersService).viewUsers("b");
    }

    @Test
    public void panel_Test() throws Exception {
        MockHttpSession session = new MockHttpSession();
        CustomUserDetails adminUser = createMockUser(Roles.ROLE_ADMIN);
        CustomUserDetails regularUser = createMockUser(Roles.ROLE_GROUP);
        session.setAttribute("dev", adminUser);
        session.setAttribute("qa", adminUser);
        session.setAttribute("nul", null);
        session.setAttribute("longName", adminUser);
        session.setAttribute("usr", regularUser);

        mockMvc.perform(get("/api/users/panel").session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("ok"))
                .andExpect(jsonPath("$.boardNames", hasSize(2)))
                .andExpect(jsonPath("$.boardNames", containsInAnyOrder("dev", "qa")));
    }

    @NotNull
    private CustomUserDetails createMockUser(@NotNull Roles role) {
        CustomUserDetails user = customUserDetails.createDefault();
        user.setRole(role);
        return user;
    }
}