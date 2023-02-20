package zoidnet.dev.chat.configuration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import zoidnet.dev.chat.controller.UserController;
import zoidnet.dev.chat.service.UserService;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.logout;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ActiveProfiles("test")
@WebMvcTest(UserController.class)
@Import(SecurityConfiguration.class)
public class SecurityConfigurationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @MockBean
    private UserDetailsService userDetailsService;

    @MockBean
    private UserService userService;


    @Test
    void shouldReturn200AfterLoginWithValidDetails() throws Exception {
        String username = "username";
        String password = "password";

        UserDetails userDetails = new User(username, passwordEncoder.encode(password), singletonList(new SimpleGrantedAuthority("USER")));
        when(userDetailsService.loadUserByUsername(username)).thenReturn(userDetails);

        mockMvc.perform(formLogin("/login").user(username).password(password))
                .andExpect(status().isOk());
    }

    @Test
    void shouldReturnLoggedInUserAfterLoginWithValidDetails() throws Exception {
        String username = "Freddie";
        String password = "password";

        UserDetails userDetails = new User(username, passwordEncoder.encode(password), singletonList(new SimpleGrantedAuthority("SUPER_USER")));
        when(userDetailsService.loadUserByUsername(username)).thenReturn(userDetails);

        mockMvc.perform(formLogin("/login").user(username).password(password))
                .andExpect(content().json("{ 'username': 'Freddie', 'authorities': [{ 'authority': 'SUPER_USER' }] }"));
    }

    @Test
    void shouldReturn401AfterLoginWithIncorrectUsername() throws Exception {
        String username = "username";
        String password = "password";
        String wrongUsername = "drowssap";

        when(userDetailsService.loadUserByUsername(wrongUsername)).thenThrow(new UsernameNotFoundException(wrongUsername));

        mockMvc.perform(formLogin("/login").user(wrongUsername).password(password))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldReturn401AfterLoginWithIncorrectPassword() throws Exception {
        String username = "username";
        String password = "password";
        String wrongPassword = "drowssap";

        UserDetails userDetails = new User(username, passwordEncoder.encode(password), emptyList());
        when(userDetailsService.loadUserByUsername(username)).thenReturn(userDetails);

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
