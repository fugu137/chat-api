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
import zoidnet.dev.chat.controller.UserController;
import zoidnet.dev.chat.repository.UserRepository;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.logout;
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


    @Test
    void shouldReturn200AfterLoginWithValidDetails() throws Exception {
        String username = "username";
        String password = "password";

        UserDetails userDetails = new User(username, passwordEncoder.encode(password), singletonList(new SimpleGrantedAuthority("USER")));
        when(userDetailsService.loadUserByUsername(username)).thenReturn(userDetails);

        mockMvc.perform(formLogin("/login").user(username).password(password))
                .andExpect(status().is(200));
    }

    @Test
    void shouldReturn401AfterLoginWithInvalidDetails() throws Exception {
        String username = "username";
        String password = "password";
        String wrongPassword = "drowssap";

        UserDetails userDetails = new User(username, passwordEncoder.encode(password), emptyList());
        when(userDetailsService.loadUserByUsername(username)).thenReturn(userDetails);

        mockMvc.perform(formLogin("/login").user(username).password(wrongPassword))
                .andExpect(status().is(401));
    }

    @Test
    void shouldReturn200AfterSuccessfulLogout() throws Exception {
        mockMvc.perform(logout())
                .andExpect(status().is(200));
    }
}
