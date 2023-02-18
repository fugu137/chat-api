package zoidnet.dev.chat.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;
import zoidnet.dev.chat.model.User;

import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;


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
        String password = "$2a$10$iYOyoRjOzo/X/ceWh/awjezp1mH20M16z56g/DY2bWfKJ5ZPxm82.";

        Optional<User> result = userRepository.findByUsername(username);

        assertThat(result.isPresent(), is(true));

        User user = result.get();

        assertThat(user.getId(), is(id));
        assertThat(user.getUsername(), is(username));
        assertThat(user.getPassword(), is(password));
    }

}
