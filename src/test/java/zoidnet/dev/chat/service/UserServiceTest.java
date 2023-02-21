package zoidnet.dev.chat.service;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import zoidnet.dev.chat.controller.dto.UserDto;
import zoidnet.dev.chat.model.User;
import zoidnet.dev.chat.repository.UserRepository;

import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.*;


@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @Captor
    private ArgumentCaptor<User> userArgumentCaptor;


    @Test
    void shouldSaveUserWithEncodedPassword() {
        String username = "testUsername";
        String password = "testPassword";
        String encodedPassword = "encodedPassword";

        UserDto userDto = new UserDto(username, password);

        when(passwordEncoder.encode(password)).thenReturn(encodedPassword);

        userService.registerUser(userDto);

        verify(userRepository, times(1)).save(userArgumentCaptor.capture());
        User capturedArgument = userArgumentCaptor.getValue();

        assertThat(capturedArgument.getUsername(), is(username));
        assertThat(capturedArgument.getPassword(), is(encodedPassword));
    }

    @Test
    void shouldNotSaveUserIfUsernameAlreadyExists() {
        String username = "duplicateUsername";
        String password = "testPassword";
        String encodedPassword = "encodedPassword";

        UserDto userDto = new UserDto(username, password);

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(new User(username, encodedPassword)));

        User savedUser = userService.registerUser(userDto);

        verify(userRepository, never()).save(any());

        assertNull(savedUser);
    }

}
