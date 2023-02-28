package zoidnet.dev.chat.controller;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import zoidnet.dev.chat.model.User;
import zoidnet.dev.chat.model.dto.PrincipalDto;
import zoidnet.dev.chat.model.dto.UserDto;
import zoidnet.dev.chat.service.UserService;


@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;


    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/principal")
    public ResponseEntity<PrincipalDto> getPrincipal(Authentication authentication) {
        if (authentication == null) return ResponseEntity.ok(null);

        UserDetails principal = (UserDetails) authentication.getPrincipal();
        PrincipalDto dto = new PrincipalDto(principal.getUsername(), principal.getAuthorities());

        return ResponseEntity.ok(dto);
    }

    @PostMapping
    public ResponseEntity<String> createUser(@RequestBody UserDto userDto) {
        try {
            User registeredUser = userService.registerUser(userDto);
            if (registeredUser != null) return ResponseEntity.status(HttpStatus.CREATED).build();

            return ResponseEntity.status(HttpStatus.CONFLICT).build();

        } catch (Exception e) {
            if (e instanceof IllegalArgumentException) return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

}
