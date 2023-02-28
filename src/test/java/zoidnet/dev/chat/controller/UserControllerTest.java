package zoidnet.dev.chat.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import zoidnet.dev.chat.configuration.SecurityConfiguration;
import zoidnet.dev.chat.model.Role;
import zoidnet.dev.chat.model.dto.UserDto;
import zoidnet.dev.chat.model.User;
import zoidnet.dev.chat.service.UserService;

import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.emptyString;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ActiveProfiles("test")
@WebMvcTest(UserController.class)
@Import(SecurityConfiguration.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private UserDetailsService userDetailsService;

    @Captor
    private ArgumentCaptor<UserDto> userDtoCaptor;


    @Test
    @WithMockUser(username = "Jacqueline", roles = "ADMIN")
    void shouldGetPrincipalIfUserLoggedIn() throws Exception {
        mockMvc.perform(get("/users/principal")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json("{ 'username': 'Jacqueline', 'authorities': ['ROLE_ADMIN']}"));
    }

    @Test
    void shouldNotGetPrincipalIfUserNotLoggedIn() throws Exception {
        mockMvc.perform(get("/users/principal")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string(emptyString()));
    }

    @Test
    void shouldCreateUser() throws Exception {
        String username = "Jacqueline";
        String password = "password";

        UserDto userDto = new UserDto(username, password);
        String userAsJson = new ObjectMapper().writeValueAsString(userDto);
        Set<Role> roles = Set.of(Role.USER);

        when(userService.registerUser(userDto)).thenReturn(new User(username, password, roles));

        mockMvc.perform(post("/users")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userAsJson))
                .andExpect(status().isCreated());

        verify(userService, times(1)).registerUser(userDtoCaptor.capture());

        UserDto capturedDto = userDtoCaptor.getValue();
        assertThat(capturedDto.getUsername(), is(username));
        assertThat(capturedDto.getPassword(), is(password));
    }

    @Test
    void shouldReturn409IfUsernameAlreadyExists() throws Exception {
        String username = "Jacqueline";
        String password = "password";

        UserDto userDto = new UserDto(username, password);
        String userAsJson = new ObjectMapper().writeValueAsString(userDto);

        when(userService.registerUser(userDto)).thenReturn(null);

        mockMvc.perform(post("/users")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userAsJson))
                .andExpect(status().isConflict());
    }

    @Test
    void shouldReturn403IfCsrfTokenIsMissing() throws Exception {
        String username = "Jacqueline";
        String password = "password";

        UserDto userDto = new UserDto(username, password);
        String userAsJson = new ObjectMapper().writeValueAsString(userDto);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userAsJson))
                .andExpect(status().isForbidden())
                .andExpect(content().string("Could not verify the provided CSRF token because no token was found to compare."));
    }

    @Test
    void shouldReturn403IfCsrfTokenIsInvalid() throws Exception {
        String username = "Jacqueline";
        String password = "password";

        UserDto userDto = new UserDto(username, password);
        String userAsJson = new ObjectMapper().writeValueAsString(userDto);

        mockMvc.perform(post("/users")
                        .with(csrf().useInvalidToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userAsJson))
                .andExpect(status().isForbidden())
                .andExpect(content().string("Invalid CSRF Token 'AQEBYGNi' was found on the request parameter '_csrf' or header 'X-CSRF-TOKEN'."));
    }

    @Test
    void shouldReturn500IfServiceThrowsError() throws Exception {
        String username = "Jacqueline";
        String password = "password";

        UserDto userDto = new UserDto(username, password);
        String userAsJson = new ObjectMapper().writeValueAsString(userDto);

        doThrow(new DataIntegrityViolationException("Invalid data")).when(userService).registerUser(userDto);

        mockMvc.perform(post("/users")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userAsJson))
                .andExpect(status().isInternalServerError());
    }

}
