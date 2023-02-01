package zoidnet.dev.chat.service;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import zoidnet.dev.chat.controller.dto.UserDto;
import zoidnet.dev.chat.model.User;
import zoidnet.dev.chat.repository.UserRepository;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @Captor
    private ArgumentCaptor<User> accountArgumentCaptor;


    @Test
    void shouldSaveUserWithEncodedPassword() {
        String username = "testUsername";
        String password = "testPassword";
        String encodedPassword = "encodedPassword";

        UserDto userDto = new UserDto(username, password);

        when(passwordEncoder.encode(password)).thenReturn(encodedPassword);

        userService.registerUser(userDto);

        verify(userRepository, times(1)).save(accountArgumentCaptor.capture());
        User capturedUser = accountArgumentCaptor.getValue();

        assertThat(capturedUser.getUsername(), is(username));
        assertThat(capturedUser.getPassword(), is(encodedPassword));
    }

}
