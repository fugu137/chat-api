package zoidnet.dev.chat.service;


import org.springframework.dao.DataAccessException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import zoidnet.dev.chat.controller.dto.UserDto;
import zoidnet.dev.chat.model.User;
import zoidnet.dev.chat.repository.UserRepository;


@Service
public class UserService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;


    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public void registerUser(UserDto userDto) throws DataAccessException {
        User newUser = userDto.toUser(passwordEncoder);
        userRepository.save(newUser);
    }

}
