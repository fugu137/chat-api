package zoidnet.dev.chat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import zoidnet.dev.chat.configuration.SecurityConfiguration;
import zoidnet.dev.chat.controller.UserController;
import zoidnet.dev.chat.repository.UserRepository;
import zoidnet.dev.chat.service.UserService;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;



@SpringBootTest
class ApplicationTests {

	@Autowired
	private SecurityConfiguration securityConfiguration;

	@Autowired
	private UserController userController;

	@Autowired
	private UserService userService;

	@Autowired
	private UserRepository userRepository;


	@Test
	void contextShouldLoad() {
		assertThat(securityConfiguration).isNotNull();
		assertThat(userController).isNotNull();
		assertThat(userService).isNotNull();
		assertThat(userRepository).isNotNull();
	}
}
