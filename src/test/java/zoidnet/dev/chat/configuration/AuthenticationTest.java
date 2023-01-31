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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;
import zoidnet.dev.chat.controller.UserController;
import zoidnet.dev.chat.repository.UserRepository;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.logout;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(UserController.class)
@Import(SecurityConfiguration.class)
public class AuthenticationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserDetailsService userDetailsService;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private UserController userController;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private WebApplicationContext webApplicationContext;


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
    void shouldReturn401AfterLoginWithInvalidDetails() throws Exception {
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
