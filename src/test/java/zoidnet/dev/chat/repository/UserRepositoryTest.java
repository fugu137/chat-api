package zoidnet.dev.chat.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.jdbc.Sql;
import zoidnet.dev.chat.model.User;

import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;


@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Sql({"classpath:user-reset.sql", "classpath:user-data.sql"})
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;


    @Test
    void shouldFindUserByUsername() {
        Long id = 1L;
        String username = "User 1";
        String password = "password1";

        Optional<User> result = userRepository.findByUsername(username);

        assertThat(result.isPresent(), is(true));

        User user = result.get();

        assertThat(user.getId(), is(id));
        assertThat(user.getUsername(), is(username));
        assertThat(user.getPassword(), is(password));
    }

    @Test
    void shouldNotFindUserByUsernameIfNoUserExists() {
        Optional<User> result = userRepository.findByUsername("Non-existent User");

        assertThat(result.isPresent(), is(false));
    }

    @Test
    void shouldSaveNewUser() {
        String username = "User 2";
        String password = "password2";

        User newUser = new User(username, password);

        User result = userRepository.save(newUser);

        assertThat(result, is(newUser));
    }

    @Test
    void shouldNotSaveNewUserIfUsernameAlreadyExists() {
        String username = "User 1";
        String password = "password";

        User newUser = new User(username, password);

        assertThrows(DataIntegrityViolationException.class, () -> userRepository.save(newUser));
    }
}
