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
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import zoidnet.dev.chat.configuration.SecurityConfiguration;
import zoidnet.dev.chat.model.Role;
import zoidnet.dev.chat.model.User;
import zoidnet.dev.chat.model.dto.UserDto;
import zoidnet.dev.chat.service.UserService;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.hamcrest.Matchers.emptyString;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.logout;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ActiveProfiles("test")
@WebMvcTest(UserController.class)
@Import({SecurityConfiguration.class, UserService.class})
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PasswordEncoder passwordEncoder;

    @MockBean
    private UserService userService;

    @Captor
    private ArgumentCaptor<UserDto> userDtoCaptor;


    @Test
    void shouldCallUserServiceOnLogin() throws Exception {
        String username = "username";
        String password = "password";
        String encodedPassword = "encodedPassword";
        Set<Role> roles = new Role(5L, "USER").asSingletonSet();

        User user = new User(username, encodedPassword, roles);

        when(passwordEncoder.matches(password, encodedPassword)).thenReturn(true);
        when(userService.loadUserByUsername(username)).thenReturn(user);

        mockMvc.perform(formLogin("/users/login").user(username).password(password));

        verify(userService).loadUserByUsername(username);
    }

    @Test
    void shouldReturn200AfterLoginWithValidDetails() throws Exception {
        String username = "username";
        String password = "password";
        String encodedPassword = "encodedPassword";
        Set<Role> roles = new Role(5L, "USER").asSingletonSet();

        User user = new User(username, encodedPassword, roles);

        when(passwordEncoder.matches(password, encodedPassword)).thenReturn(true);
        when(userService.loadUserByUsername(username)).thenReturn(user);

        mockMvc.perform(formLogin("/users/login").user(username).password(password))
                .andExpect(status().isOk());
    }

    @Test
    void shouldReturnLoggedInUserAfterLoginWithValidDetails() throws Exception {
        String username = "Freddie";
        String password = "chicken";
        String encodedPassword = "encodedPassword";
        Set<Role> roles = Role.USER.asSingletonSet();

        User user = new User(username, encodedPassword, roles);

        when(passwordEncoder.matches(password, encodedPassword)).thenReturn(true);
        when(userService.loadUserByUsername(username)).thenReturn(user);

        mockMvc.perform(formLogin("/users/login").user(username).password(password))
                .andExpect(content().json("{ 'username': 'Freddie', 'authorities': ['ROLE_USER'] }"));
    }

    @Test
    void shouldReturnLoggedInUserWithMultipleRolesAfterLoginWithValidDetails() throws Exception {
        String username = "userAdmin";
        String password = "password123";
        String encodedPassword = "encodedPassword";

        Role editor = new Role(1L, "EDITOR");
        Role advisor = new Role(2L, "ADVISOR");
        Set<Role> roles = new HashSet<>(List.of(editor, advisor));

        User user = new User(username, encodedPassword, roles);

        when(passwordEncoder.matches(password, encodedPassword)).thenReturn(true);
        when(userService.loadUserByUsername(username)).thenReturn(user);

        mockMvc.perform(formLogin("/users/login").user(username).password(password))
                .andExpect(content().json("{ 'username': 'userAdmin', 'authorities': ['ROLE_EDITOR', 'ROLE_ADVISOR'] }"));
    }

    @Test
    void shouldReturn401AfterLoginWithIncorrectUsername() throws Exception {
        String wrongUsername = "drowssap";
        String password = "password";

        when(userService.loadUserByUsername(wrongUsername)).thenThrow(new UsernameNotFoundException(wrongUsername));

        mockMvc.perform(formLogin("/users/login").user(wrongUsername).password(password))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Bad credentials"));
    }

    @Test
    void shouldReturn401AfterLoginWithIncorrectPassword() throws Exception {
        String username = "username";
        String encodedPassword = "encodedPassword";
        String wrongPassword = "drowssap";
        Set<Role> roles = Role.USER.asSingletonSet();

        User user = new User(username, encodedPassword, roles);

        when(passwordEncoder.matches(wrongPassword, encodedPassword)).thenReturn(false);
        when(userService.loadUserByUsername(username)).thenReturn(user);

        mockMvc.perform(formLogin("/users/login").user(username).password(wrongPassword))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Bad credentials"));
    }

    @Test
    void shouldReturn403AfterLoginWithMissingCsrfToken() throws Exception {
        String username = "username";
        String password = "password";
        String encodedPassword = "encodedPassword";
        Set<Role> roles = Role.USER.asSingletonSet();

        User user = new User(username, encodedPassword, roles);

        when(passwordEncoder.matches(password, encodedPassword)).thenReturn(true);
        when(userService.loadUserByUsername(username)).thenReturn(user);

        mockMvc.perform(post("/users/login")
                        .param("username", username)
                        .param("password", password))
                .andExpect(status().isForbidden())
                .andExpect(content().string("Could not verify the provided CSRF token because no token was found to compare."));
    }

    @Test
    void shouldReturn403AfterLoginWithInvalidCsrfToken() throws Exception {
        String username = "username";
        String password = "password";
        String encodedPassword = "encodedPassword";
        Set<Role> roles = Role.USER.asSingletonSet();

        User user = new User(username, encodedPassword, roles);

        when(passwordEncoder.matches(password, encodedPassword)).thenReturn(true);
        when(userService.loadUserByUsername(username)).thenReturn(user);

        mockMvc.perform(post("/users/login")
                        .param("username", username)
                        .param("password", password)
                        .with(csrf().useInvalidToken()))
                .andExpect(status().isForbidden())
                .andExpect(content().string("Invalid CSRF Token 'AQEBYGNi' was found on the request parameter '_csrf' or header 'X-CSRF-TOKEN'."));
    }

    @Test
    void shouldAllowRequestsFromCorsAllowedOrigins() throws Exception {
        mockMvc.perform(options("/users/login")
                        .header("Access-Control-Request-Method", "GET")
                        .header("Origin", "http://allowed-url.com"))
                .andExpect(status().isOk());
    }

    @Test
    void shouldNotAllowRequestsFromNonCorsAllowedOrigins() throws Exception {
        mockMvc.perform(options("/users/login")
                        .header("Access-Control-Request-Method", "GET")
                        .header("Origin", "http://non-allowed-url.com"))
                .andExpect(status().isForbidden())
                .andExpect(content().string("Invalid CORS request"));
    }

    @Test
    @WithMockUser(username = "Jacqueline", roles = "USER")
    void shouldReturn200AfterSuccessfulLogout() throws Exception {
        mockMvc.perform(logout().logoutUrl("/users/logout"))
                .andExpect(status().isOk());
    }

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
    void shouldReturn201AfterCreatingUser() throws Exception {
        String username = "Jacqueline";
        String password = "password";
        Set<Role> roles = new Role(1L, "USER").asSingletonSet();

        UserDto userDto = new UserDto(username, password);
        String userAsJson = new ObjectMapper().writeValueAsString(userDto);

        when(userService.registerUser(userDto)).thenReturn(new User(username, password, roles));

        mockMvc.perform(post("/users")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userAsJson))
                .andExpect(status().isCreated());

        verify(userService).registerUser(userDto);
    }

    @Test
    void shouldReturn409AfterCreatingUserWithUsernameThatAlreadyExists() throws Exception {
        String username = "Jacqueline";
        String password = "password";

        UserDto userDto = new UserDto(username, password);
        String userAsJson = new ObjectMapper().writeValueAsString(userDto);

        when(userService.registerUser(userDto)).thenThrow(new DataIntegrityViolationException("Username already exists"));

        mockMvc.perform(post("/users")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userAsJson))
                .andExpect(status().isConflict());
    }

    @Test
    void shouldReturn403AfterCreatingUserWithMissingCsrfToken() throws Exception {
        String username = "Jacqueline";
        String password = "password";

        UserDto userDto = new UserDto(username, password);
        String userAsJson = new ObjectMapper().writeValueAsString(userDto);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userAsJson))
                .andExpect(status().isForbidden())
                .andExpect(content().string("Could not verify the provided CSRF token because no token was found to compare."));

        verify(userService, never()).registerUser(userDto);
    }

    @Test
    void shouldReturn403AfterCreatingUserWithInvalidCsrfToken() throws Exception {
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

        verify(userService, never()).registerUser(userDto);
    }

    @Test
    void shouldReturn400AfterCreatingUserWithInvalidDto() throws Exception {
        String username = "Jacqueline";
        String password = "password";

        UserDto invalidDto = new UserDto(username, password);
        String userAsJson = new ObjectMapper().writeValueAsString(invalidDto);

        doThrow(new IllegalArgumentException("Argument not valid")).when(userService).registerUser(invalidDto);

        mockMvc.perform(post("/users")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userAsJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn500AfterCreatingUserWithInternalError() throws Exception {
        String username = "Jacqueline";
        String password = "password";

        UserDto userDto = new UserDto(username, password);
        String userAsJson = new ObjectMapper().writeValueAsString(userDto);

        doThrow(new RuntimeException("Runtime exception")).when(userService).registerUser(userDto);

        mockMvc.perform(post("/users")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userAsJson))
                .andExpect(status().isInternalServerError());
    }

}
