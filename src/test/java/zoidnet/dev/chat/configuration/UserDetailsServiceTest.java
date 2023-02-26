package zoidnet.dev.chat.configuration;


import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import zoidnet.dev.chat.repository.UserRepository;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;


@ActiveProfiles("test")
@SpringBootTest
@Transactional
@Sql({"classpath:user-reset.sql", "classpath:user-data.sql"})
public class UserDetailsServiceTest {

    @Autowired
    private UserDetailsService userDetailsService;

    @SpyBean
    private UserRepository userRepository;


    @Test
    void shouldLoadUserFromDatabase() {
        String username = "User 1";
        String password = "$2a$10$iYOyoRjOzo/X/ceWh/awjezp1mH20M16z56g/DY2bWfKJ5ZPxm82.";

        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        verify(userRepository, times(1)).findByUsername(username);

        assertThat(userDetails.getUsername(), is(username));
        assertThat(userDetails.getPassword(), is(password));
    }

    @Test
    void shouldThrowIfUsernameDoesNotExist() {
        String username = "wrongUsername";

        assertThrows(UsernameNotFoundException.class, () -> userDetailsService.loadUserByUsername(username));
    }
}
