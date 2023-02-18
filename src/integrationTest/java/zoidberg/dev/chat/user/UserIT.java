package zoidberg.dev.chat.user;


import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import zoidnet.dev.chat.Application;


@SpringBootTest(classes = Application.class)
public class UserIT {

    @Test
    public void shouldWork() {
       assert(1 == 1);
    }
}
