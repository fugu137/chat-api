package zoidnet.dev.chat.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;
import zoidnet.dev.chat.model.User;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;


@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Sql({"classpath:user-reset.sql", "classpath:user-data.sql"})
public class UserRepositoryIT {

    @Autowired
    private UserRepository userRepository;

    @Test
    void shouldFindAllUsers() {
        List<User> users = userRepository.findAll();

        User user1 = users.get(0);
        User user2 = users.get(1);

        assertThat(user1.getId(), is(1L));
        assertThat(user1.getUsername(), is("User 1"));
        assertThat(user1.getPassword(), is("password1"));

        assertThat(user2.getId(), is(2L));
        assertThat(user2.getUsername(), is("User 2"));
        assertThat(user2.getPassword(), is("password2"));
    }

    @Test
    void shouldFindUserById() {
        Long id = 1L;
        String username = "User 1";
        String password = "password1";

        User expectedUser = new User(id, username, password);

        Optional<User> result = userRepository.findById(id);

        assertThat(result.isPresent(), is(true));
        assertThat(result.get(), is(expectedUser));
    }

    @Test
    void shouldSaveUser() {
        User user = new User("testUser", "testPassword");

        userRepository.save(user);

        Optional<User> result = userRepository.findById(user.getId());

        assertThat(result.isPresent(), is(true));
        assertThat(result.get(), is(user));
    }
}
