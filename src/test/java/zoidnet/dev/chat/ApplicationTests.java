package zoidnet.dev.chat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import zoidnet.dev.chat.repository.UserRepository;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;



@SpringBootTest
class ApplicationTests {

	@Autowired
	private UserRepository userRepository;

	@Test
	void contextShouldLoad() {
		assertThat(userRepository).isNotNull();
	}
}
