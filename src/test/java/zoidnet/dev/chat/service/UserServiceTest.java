package zoidnet.dev.chat.service;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import zoidnet.dev.chat.model.Role;
import zoidnet.dev.chat.model.User;
import zoidnet.dev.chat.model.dto.UserDto;
import zoidnet.dev.chat.repository.RoleRepository;
import zoidnet.dev.chat.repository.UserRepository;

import java.util.Optional;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.*;


@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

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
        when(roleRepository.findByName("USER")).thenReturn(Optional.of(new Role(1L, "USER")));

        userService.registerUser(userDto);

        verify(userRepository).save(userArgumentCaptor.capture());
        User capturedArgument = userArgumentCaptor.getValue();

        assertThat(capturedArgument.getUsername(), is(username));
        assertThat(capturedArgument.getPassword(), is(encodedPassword));
    }

    @Test
    void shouldSaveUserWithDefaultUserRole() {
        String username = "testUsername";
        String password = "testPassword";
        String encodedPassword = "encodedPassword";

        UserDto userDto = new UserDto(username, password);

        when(passwordEncoder.encode(password)).thenReturn(encodedPassword);
        when(roleRepository.findByName("USER")).thenReturn(Optional.empty());

        userService.registerUser(userDto);

        verify(userRepository).save(userArgumentCaptor.capture());

        Set<Role> roles = userArgumentCaptor.getValue().getRoles();
        assertThat(roles, hasSize(1));
        assertThat(roles, contains(equalToObject(Role.USER)));
    }

    @Test
    void shouldThrowDataIntegrityExceptionIfUsernameAlreadyExists() {
        String username = "duplicateUsername";
        String password = "testPassword";

        UserDto userDto = new UserDto(username, password);

        when(userRepository.save(any(User.class))).thenThrow(DataIntegrityViolationException.class);

        assertThrows(DataIntegrityViolationException.class, () -> userService.registerUser(userDto));
    }

    @Test
    void shouldGetUserIfUserExists() {
        String username = "existingUser";
        String password = "userPassword";

        UserDto userDto = new UserDto(username, password);
        User userFromRepository = new User(username, password, Role.USER.asSingletonSet());

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(userFromRepository));

        User user = userService.loadUserByUsername(username);

        assertThat(user.getUsername(), is(userDto.getUsername()));
        assertThat(user.getPassword(), is(userDto.getPassword()));
        assertThat(user.getAuthorities(), hasSize(1));
        assertThat(user.getAuthorities(), contains(equalToObject(new SimpleGrantedAuthority(Role.USER.toAuthority()))));
    }

    @Test
    void shouldThrowUsernameNotFoundExceptionIfUserDoesNotExist() {
        String wrongUsername = "wrongUsername";

        when(userRepository.findByUsername(wrongUsername)).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> userService.loadUserByUsername(wrongUsername));
    }

}
