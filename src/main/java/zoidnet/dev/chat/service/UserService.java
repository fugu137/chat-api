package zoidnet.dev.chat.service;


import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import zoidnet.dev.chat.model.dto.UserDto;
import zoidnet.dev.chat.model.User;
import zoidnet.dev.chat.model.Role;
import zoidnet.dev.chat.repository.RoleRepository;
import zoidnet.dev.chat.repository.UserRepository;

import java.util.Optional;
import java.util.Set;


@Service
public class UserService {

    private final UserRepository userRepository;

    private final RoleRepository roleRepository;

    private final PasswordEncoder passwordEncoder;


    public UserService(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User registerUser(UserDto userDto) {
        Optional<User> existingUser = userRepository.findByUsername(userDto.username());

        if (existingUser.isPresent()) return null;

        Role userRole = roleRepository.findByName("USER").orElseGet(() -> roleRepository.save(Role.USER));
        Set<Role> roles = Set.of(userRole);

        User newUser = userDto.toUser(passwordEncoder, roles);

        userRepository.save(newUser);

        return newUser;
    }

}
