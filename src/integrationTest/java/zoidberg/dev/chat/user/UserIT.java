package zoidberg.dev.chat.user;


import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import zoidnet.dev.chat.Application;
import zoidnet.dev.chat.controller.dto.UserDto;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ActiveProfiles("integration")
@SpringBootTest(classes = Application.class)
@AutoConfigureMockMvc
@Transactional
@Sql({"classpath:user-reset.sql", "classpath:user-data.sql"})
public class UserIT {

    @Autowired
    private MockMvc mockMvc;


    @Test
    public void shouldLoginWithExistingUser() throws Exception {
        String username = "User 1";
        String password = "password1";

        mockMvc.perform(formLogin("/login").user(username).password(password))
                .andExpect(status().isOk())
                .andExpect(content().json("{ 'username': 'User 1', 'authorities': [{ 'authority': 'ROLE_USER' }] }"));
    }

    @Test
    void shouldNotLoginWithNonExistentUser() throws Exception {
        String username = "Non-existent User";
        String password = "password";

        mockMvc.perform(formLogin("/login").user(username).password(password))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldNotLoginWithIncorrectPassword() throws Exception {
        String username = "User 1";
        String password = "wrongPassword";

        mockMvc.perform(formLogin("/login").user(username).password(password))
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
    }

}
