package zoidnet.dev.chat.service;


import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import zoidnet.dev.chat.model.dto.UserDto;
import zoidnet.dev.chat.model.User;
import zoidnet.dev.chat.model.Role;
import zoidnet.dev.chat.repository.UserRepository;

import java.util.Optional;
import java.util.Set;


@Service
public class UserService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;


    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User registerUser(UserDto userDto) throws IllegalArgumentException {
        Optional<User> existingUser = userRepository.findByUsername(userDto.username());

        if (existingUser.isPresent()) return null;

        Set<Role> roles = Set.of(Role.USER);
        User newUser = userDto.toUser(passwordEncoder, roles);

        userRepository.insert(newUser);

        return newUser;
    }

}
