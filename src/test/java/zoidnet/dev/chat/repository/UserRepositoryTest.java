package zoidnet.dev.chat.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.core.AutoConfigureCache;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import zoidnet.dev.chat.model.Role;
import zoidnet.dev.chat.model.User;

import java.util.Optional;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;


@ActiveProfiles("test")
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
//@DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
@Sql("classpath:user-data.sql")
@Sql(scripts = "classpath:user-reset.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;


    @Test
    void shouldFindUserByUsername() {
        Long id = 2L;
        String username = "User 1";
        String password = "$2a$10$iYOyoRjOzo/X/ceWh/awjezp1mH20M16z56g/DY2bWfKJ5ZPxm82.";

        Optional<User> result = userRepository.findByUsername(username);

        assertThat(result.isPresent(), is(true));

        User user = result.get();

        assertThat(user.getId(), is(id));
        assertThat(user.getUsername(), is(username));
        assertThat(user.getPassword(), is(password));
        assertThat(user.getRoles(), contains(equalToObject(Role.USER)));
    }

    @Test
    void shouldFindAdminByUsername() {
        Long id = 1L;
        String username = "Admin";
        String password = "$2a$10$zVd5EoYxTPKOoj6sGrYvAud86v5oF/l5TlYg4MAEvNt9Ct.Db91O.";

        Optional<User> result = userRepository.findByUsername(username);

        assertThat(result.isPresent(), is(true));

        User user = result.get();

        assertThat(user.getId(), is(id));
        assertThat(user.getUsername(), is(username));
        assertThat(user.getPassword(), is(password));
        assertThat(user.getRoles(), contains(equalToObject(Role.ADMIN)));
    }

    @Test
    void shouldNotFindUserByUsernameIfNoUserExists() {
        Optional<User> result = userRepository.findByUsername("Non-existent User");

        assertThat(result.isPresent(), is(false));
    }

    @Test
    void shouldSaveNewUser() {
        String username = "User 3";
        String password = "password3";

        User newUser = new User(username, password, Set.of(Role.USER));

        assertDoesNotThrow(() -> userRepository.insert(newUser));
    }

    @Test
    void shouldNotSaveNewUserIfUsernameAlreadyExists() {
        String username = "User 1";
        String password = "password";

        User newUser = new User(username, password, Set.of(Role.USER));

        assertThrows(DataIntegrityViolationException.class, () -> userRepository.insert(newUser));
    }
}
