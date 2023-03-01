package zoidnet.dev.chat.configuration;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import zoidnet.dev.chat.model.Role;
import zoidnet.dev.chat.model.User;
import zoidnet.dev.chat.model.dto.UserDto;
import zoidnet.dev.chat.repository.UserRepository;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.logout;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ActiveProfiles("test")
@WebMvcTest(value = SecurityConfiguration.class)
public class SecurityConfigurationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserDetailsService userDetailsService;

    @MockBean
    private UserRepository userRepository;


    @Test
    void shouldReturn200AfterLoginWithValidDetails() throws Exception {
        String username = "username";
        String password = "password";
        Set<Role> roles = new Role(5L, "USER").asSingletonSet();

        UserDto userDto = new UserDto(username, password);
        User user = userDto.toUser(passwordEncoder, roles);

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

        mockMvc.perform(formLogin("/login").user(username).password(password))
                .andExpect(status().isOk());
    }

    @Test
    void shouldReturnLoggedInUserAfterLoginWithValidDetails() throws Exception {
        String username = "Freddie";
        String password = "chicken";

        UserDto userDto = new UserDto(username, password);
        User user = userDto.toUser(passwordEncoder, Role.USER.asSingletonSet());

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

        mockMvc.perform(formLogin("/login").user(username).password(password))
                .andExpect(content().json("{ 'username': 'Freddie', 'authorities': ['ROLE_USER'] }"));
    }

    @Test
    void shouldReturnLoggedInUserWithMultipleRolesAfterLoginWithValidDetails() throws Exception {
        String username = "userAdmin";
        String password = "password123";

        Role editor = new Role(1L, "EDITOR");
        Role advisor = new Role(2L, "ADVISOR");

        Set<Role> roles = new HashSet<>(List.of(editor, advisor));

        UserDto userDto = new UserDto(username, password);
        User user = userDto.toUser(passwordEncoder, roles);

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

        mockMvc.perform(formLogin("/login").user(username).password(password))
                .andExpect(content().json("{ 'username': 'userAdmin', 'authorities': ['ROLE_EDITOR', 'ROLE_ADVISOR'] }"));
    }

    @Test
    void shouldCheckRepositoryForUserDetailsOnLogin() throws Exception {
        String username = "username";
        String password = "password";

        mockMvc.perform(formLogin("/login")
                .user(username)
                .password(password)
        );

        verify(userRepository, times(1)).findByUsername(username);
    }

    @Test
    void shouldThrowUsernameNotFoundExceptionFromUserDetailsServiceAfterLoginWithIncorrectUsername() throws Exception {
        String wrongUsername = "wrongUsername";
        String password = "password";

        mockMvc.perform(formLogin("/login")
                .user(wrongUsername)
                .password(password)
        );

        assertThrows(UsernameNotFoundException.class, () -> userDetailsService.loadUserByUsername(wrongUsername));
    }

    @Test
    void shouldReturn401AfterLoginWithIncorrectUsername() throws Exception {
        String wrongUsername = "drowssap";
        String password = "password";

        when(userRepository.findByUsername(wrongUsername)).thenReturn(Optional.empty());

        mockMvc.perform(formLogin("/login").user(wrongUsername).password(password))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Bad credentials"));
    }

    @Test
    void shouldReturn401AfterLoginWithIncorrectPassword() throws Exception {
        String username = "username";
        String password = "password";
        String wrongPassword = "drowssap";

        UserDto userDto = new UserDto(username, password);
        User user = userDto.toUser(passwordEncoder, Role.USER.asSingletonSet());

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

        mockMvc.perform(formLogin("/login").user(username).password(wrongPassword))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Bad credentials"));
    }

    @Test
    void shouldReturn403AfterLoginWithMissingCsrfToken() throws Exception {
        String username = "username";
        String password = "password";

        UserDto userDto = new UserDto(username, password);
        User user = userDto.toUser(passwordEncoder, Role.USER.asSingletonSet());

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

        mockMvc.perform(post("/login")
                        .param("username", username)
                        .param("password", password))
                .andExpect(status().isForbidden())
                .andExpect(content().string("Could not verify the provided CSRF token because no token was found to compare."));
    }

    @Test
    void shouldReturn403AfterLoginWithInvalidCsrfToken() throws Exception {
        String username = "username";
        String password = "password";

        UserDto userDto = new UserDto(username, password);
        User user = userDto.toUser(passwordEncoder, Role.USER.asSingletonSet());

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

        mockMvc.perform(post("/login")
                        .param("username", username)
                        .param("password", password)
                        .with(csrf().useInvalidToken()))
                .andExpect(status().isForbidden())
                .andExpect(content().string("Invalid CSRF Token 'AQEBYGNi' was found on the request parameter '_csrf' or header 'X-CSRF-TOKEN'."));
    }

    @Test
    void shouldReturn200AfterSuccessfulLogout() throws Exception {
        mockMvc.perform(logout())
                .andExpect(status().isOk());
    }

    @Test
    void shouldAllowRequestsFromCorsAllowedOrigins() throws Exception {
        mockMvc.perform(options("/login")
                        .header("Access-Control-Request-Method", "GET")
                        .header("Origin", "http://allowed-url.com"))
                .andExpect(status().isOk());
    }

    @Test
    void shouldNotAllowRequestsFromNonCorsAllowedOrigins() throws Exception {
        mockMvc.perform(options("/login")
                        .header("Access-Control-Request-Method", "GET")
                        .header("Origin", "http://non-allowed-url.com"))
                .andExpect(status().isForbidden())
                .andExpect(content().string("Invalid CORS request"));
    }
}
