package zoidnet.dev.chat.configuration;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import zoidnet.dev.chat.Application;
import zoidnet.dev.chat.controller.dto.UserDto;
import zoidnet.dev.chat.model.User;
import zoidnet.dev.chat.repository.UserRepository;

import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.logout;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ActiveProfiles("test")
@WebMvcTest(Application.class)
@Import(SecurityConfiguration.class)
public class SecurityConfigurationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserDetailsService userDetailsService;

    @MockBean
    private UserRepository userRepository;

    @Captor
    private ArgumentCaptor<String> argumentCaptor;


    @Test
    void userDetailsServiceShouldLoadUserFromDatabase() {
        String username = "username";
        String password = "password";

        UserDto userDto = new UserDto(username, password);
        User user = userDto.toUser(passwordEncoder);

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

        userDetailsService.loadUserByUsername(username);

        verify(userRepository, times(1)).findByUsername(argumentCaptor.capture());

        assertThat(argumentCaptor.getValue(), is(username));
    }

    @Test
    void userDetailsServiceShouldThrowIfUsernameDoesNotExist() {
        String username = "wrongUsername";

        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> userDetailsService.loadUserByUsername(username));
    }

    @Test
    void shouldReturn200AfterLoginWithValidDetails() throws Exception {
        String username = "username";
        String password = "password";

        UserDto userDto = new UserDto(username, password);
        User user = userDto.toUser(passwordEncoder);

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

        mockMvc.perform(formLogin("/login").user(username).password(password))
                .andExpect(status().isOk());
    }

    @Test
    void shouldReturnLoggedInUserAfterLoginWithValidDetails() throws Exception {
        String username = "Freddie";
        String password = "chicken";

        UserDto userDto = new UserDto(username, password);
        User user = userDto.toUser(passwordEncoder);

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

        mockMvc.perform(formLogin("/login").user(username).password(password))
                .andExpect(content().json("{ 'username': 'Freddie', 'authorities': [{ 'authority': 'ROLE_USER' }] }"));
    }

    @Test
    void shouldReturn401AfterLoginWithIncorrectUsername() throws Exception {
        String wrongUsername = "drowssap";
        String password = "password";

        when(userRepository.findByUsername(wrongUsername)).thenReturn(Optional.empty());

        mockMvc.perform(formLogin("/login").user(wrongUsername).password(password))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldReturn401AfterLoginWithIncorrectPassword() throws Exception {
        String username = "username";
        String password = "password";
        String wrongPassword = "drowssap";

        UserDto userDto = new UserDto(username, password);
        User user = userDto.toUser(passwordEncoder);

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

        mockMvc.perform(formLogin("/login").user(username).password(wrongPassword))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldReturn403AfterLoginWithMissingCsrfToken() throws Exception {
        String username = "username";
        String password = "password";

        mockMvc.perform(post("/login")
                .param("username", username)
                .param("password", password))
        .andExpect(status().isForbidden());

    }

    @Test
    void shouldReturn200AfterSuccessfulLogout() throws Exception {
        mockMvc.perform(logout())
                .andExpect(status().isOk());
    }
}
