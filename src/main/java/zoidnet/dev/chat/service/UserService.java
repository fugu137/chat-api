package zoidnet.dev.chat.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import zoidnet.dev.chat.controller.dto.UserDto;
import zoidnet.dev.chat.model.User;
import zoidnet.dev.chat.repository.UserRepository;

import java.util.Optional;


@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;


    public void registerUser(UserDto userDto) {
        User newUser = userDto.toUser(passwordEncoder);
        userRepository.save(newUser);
    }

    public Optional<User> findUser(Long accountId) {
        return userRepository.findById(accountId);
    }

}
