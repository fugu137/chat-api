package zoidnet.dev.chat.repository;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import zoidnet.dev.chat.model.Role;

import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;



@ActiveProfiles("test")
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Sql({"classpath:reset.sql", "classpath:data.sql"})
public class RoleRepositoryTest {

    @Autowired
    private RoleRepository roleRepository;


    @Test
    void shouldFindRoleByName() {
        Long id = 2L;
        String roleName = "USER";

        Optional<Role> result = roleRepository.findByName(roleName);

        assertThat(result.isPresent(), is(true));

        Role role = result.get();
        assertThat(role.getId(), is(id));
        assertThat(role.getName(), is(roleName));
    }

    @Test
    void shouldNotFindRoleByNameIfRoleDoesNotExist() {
        Optional<Role> result = roleRepository.findByName("NON_EXISTENT_ROLE");

        assertThat(result.isPresent(), is(false));
    }

}
