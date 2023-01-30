package zoidnet.dev.chat.controller.dto;


import org.springframework.security.crypto.password.PasswordEncoder;
import zoidnet.dev.chat.model.User;


public record UserDto(String username, String password) {

    public String getUsername() {
        return this.username;
    }

    public String getPassword() {
        return this.password;
    }

    public User toUser(PasswordEncoder passwordEncoder) {
        return new User(username, passwordEncoder.encode(password));
    }
}
