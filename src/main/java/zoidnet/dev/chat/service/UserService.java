package zoidnet.dev.chat.service;


import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import zoidnet.dev.chat.model.Role;
import zoidnet.dev.chat.model.User;
import zoidnet.dev.chat.model.dto.UserDto;
import zoidnet.dev.chat.repository.RoleRepository;
import zoidnet.dev.chat.repository.UserRepository;

import java.util.Set;


@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;

    private final RoleRepository roleRepository;

    private final PasswordEncoder passwordEncoder;


    public UserService(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User registerUser(UserDto userDto) {
        Role userRole = roleRepository.findByName(Role.USER.getName()).orElseGet(() -> Role.USER);
        Set<Role> roles = userRole.asSingletonSet();

        User newUser = userDto.toUser(passwordEncoder, roles);

        return userRepository.save(newUser);
    }

    @Override
    public User loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository
                .findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User '" + username + "' does not exist"));
    }

}
