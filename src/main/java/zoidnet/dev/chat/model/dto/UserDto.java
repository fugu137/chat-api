package zoidnet.dev.chat.model.dto;


import org.springframework.security.crypto.password.PasswordEncoder;
import zoidnet.dev.chat.model.User;
import zoidnet.dev.chat.model.Role;

import java.util.Set;


public record UserDto(String username, String password) {

    public String getUsername() {
        return this.username;
    }

    public String getPassword() {
        return this.password;
    }

    public User toUser(PasswordEncoder passwordEncoder, Set<Role> roles) {
        return new User(username, passwordEncoder.encode(password), roles);
    }
}
