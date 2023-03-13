package zoidberg.dev.chat.user;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import zoidnet.dev.chat.Application;
import zoidnet.dev.chat.model.User;
import zoidnet.dev.chat.model.dto.UserDto;
import zoidnet.dev.chat.repository.UserRepository;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ActiveProfiles("test")
@SpringBootTest(classes = Application.class)
@AutoConfigureMockMvc
@Sql({"classpath:reset.sql", "classpath:data.sql"})
public class UserIT {

    @Autowired
    private MockMvc mockMvc;

    @SpyBean
    private UserRepository userRepository;


    @AfterEach
    void clearSpyInvocations() {
        clearInvocations(userRepository);
    }

    @Test
    public void shouldLoginWithExistingUser() throws Exception {
        String username = "User 1";
        String password = "password1";

        mockMvc.perform(formLogin("/users/login").user(username).password(password))
                .andExpect(status().isOk())
                .andExpect(content().json("{ 'username': 'User 1', 'authorities': ['ROLE_USER'] }"));
    }

    @Test
    public void shouldLoginWithExistingAdmin() throws Exception {
        String username = "Admin";
        String password = "adminPassword";

        mockMvc.perform(formLogin("/users/login").user(username).password(password))
                .andExpect(status().isOk())
                .andExpect(content().json("{ 'username': 'Admin', 'authorities': ['ROLE_USER', 'ROLE_ADMIN'] }"));
    }

    @Test
    void shouldNotLoginWithNonExistentUser() throws Exception {
        String username = "Non-existent User";
        String password = "password";

        mockMvc.perform(formLogin("/users/login").user(username).password(password))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldNotLoginWithIncorrectPassword() throws Exception {
        String username = "User 1";
        String password = "wrongPassword";

        mockMvc.perform(formLogin("/users/login").user(username).password(password))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithUserDetails("User 1")
    void shouldGetPrincipalIfLoggedIn() throws Exception {
        mockMvc.perform(get("/users/principal")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().json("{ 'username': 'User 1', 'authorities': ['ROLE_USER'] }"));
    }

    @Test
    void shouldNotGetPrincipalIfNotLoggedIn() throws Exception {
        mockMvc.perform(get("/users/principal")
                        .with(csrf()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void shouldCreateNewUser() throws Exception {
        String username = "Jacqueline";
        String password = "password";

        UserDto userDto = new UserDto(username, password);
        String userAsJson = new ObjectMapper().writeValueAsString(userDto);

        mockMvc.perform(post("/users")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userAsJson))
                .andExpect(status().isCreated());

        mockMvc.perform(formLogin("/users/login").user(username).password(password))
                .andExpect(status().isOk());
    }

    @Test
    void shouldNotCreateNewUserIfUsernameAlreadyExists() throws Exception {
        String username = "User 1";
        String password = "differentPassword";

        UserDto userDto = new UserDto(username, password);
        String userAsJson = new ObjectMapper().writeValueAsString(userDto);

        mockMvc.perform(post("/users")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userAsJson))
                .andExpect(status().isConflict());

        verify(userRepository, never()).save(any(User.class));
    }

}
