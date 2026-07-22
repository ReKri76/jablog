package com.example.jablog.http;

import com.example.jablog.DTO.Login;
import com.example.jablog.config.security.CustomUserDetails;
import com.example.jablog.config.security.Roles;
import com.example.jablog.controllers.Users;
import com.example.jablog.repository.UserDetailsRepository;
import com.example.jablog.service.CustomUserDetailsService;
import com.example.jablog.service.UsersService;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import java.util.ArrayList;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class UsersTest {

    @Mock
    private UsersService usersService;

    @InjectMocks
    private Users users;

    private MockMvc mockMvc;

    private final UserDetailsRepository  userDetailsRepository =  mock(UserDetailsRepository.class);
    private final CustomUserDetailsService customUserDetails = new CustomUserDetailsService(userDetailsRepository);

    @BeforeEach
    void setUp() {
        InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
        viewResolver.setPrefix("/WEB-INF/templates/");
        viewResolver.setSuffix(".html");
        mockMvc = MockMvcBuilders.standaloneSetup(users).setViewResolvers(viewResolver).build();
    }

    @Test
    public void addUser_Test() throws Exception {
        doNothing().when(usersService).addUser(anyString(), any(Login.class));

        mockMvc.perform(post("/users/panel/b/add")
                .param("nickname", "nickname")
                .param("password", "password")
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/users/panel/b"));

        verify(usersService).addUser(anyString(), any(Login.class));
    }

    @Test
    public void deleteUser_Test() throws Exception {
        doNothing().when(usersService).deleteUser(anyString(), anyString());

        mockMvc.perform(delete("/users/panel/b/add"))
                .andExpect(status().isOk())
                .andExpect(header().string("HX-Redirect", "/users/panel/b"));

        verify(usersService).deleteUser(anyString(), anyString());
    }

    @Test
    public void viewUsers_Test() throws Exception {
        final ArrayList<String> users = new ArrayList<>();
        users.add("n1");
        users.add("n2");
        users.add("n3");
        users.add("n4");
        when(usersService.viewUsers("b")).thenReturn(users);

        mockMvc.perform(get("/users/panel/b"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("users", users))
                .andExpect(view().name("users"))
                .andExpect(model().attribute("boardName", "b"))
                .andExpect(model().attributeExists("login"));
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

        mockMvc.perform(get("/users/panel").session(session))
                .andExpect(status().isOk())
                .andExpect(view().name("panel"))
                .andExpect(model().attributeExists("login"))
                .andExpect(model().attribute("boardNames", hasSize(2)))
                .andExpect(model().attribute("boardNames", containsInAnyOrder("dev", "qa")));
    }

    @NotNull
    private CustomUserDetails createMockUser(@NotNull Roles role) {
        CustomUserDetails user = customUserDetails.createDefault();
        user.setRole(role);
        return user;
    }
}
