package zoidnet.dev.chat;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.userdetails.UserDetailsService;
import zoidnet.dev.chat.configuration.SecurityConfiguration;
import zoidnet.dev.chat.repository.UserRepository;
import zoidnet.dev.chat.service.UserService;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;



@SpringBootTest
class ApplicationTests {


	@Autowired
	private SecurityConfiguration securityConfiguration;

	@MockBean
	private UserDetailsService userDetailsService;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private UserService userService;


	@Test
	void contextShouldLoad() {
		assertThat(securityConfiguration).isNotNull();
		assertThat(userRepository).isNotNull();
		assertThat(userService).isNotNull();
	}
}
