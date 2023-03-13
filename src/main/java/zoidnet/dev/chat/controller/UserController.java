package zoidnet.dev.chat.controller;


import org.apache.commons.lang3.NotImplementedException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import zoidnet.dev.chat.model.dto.PrincipalDto;
import zoidnet.dev.chat.model.dto.UserDto;
import zoidnet.dev.chat.service.UserService;


@RestController
@RequestMapping("/users")
public class UserController implements IUserController {

    private final UserService userService;


    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping(path = "/login", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ResponseEntity<PrincipalDto> login(@RequestBody UserDto userDto) {
        throw new NotImplementedException("This method should not be called. It is implemented by Spring Security filters.");
    }

    @PostMapping("/logout")
    public void logout() {
        throw new NotImplementedException("This method should not be called. It is implemented by Spring Security filters.");
    }

    @GetMapping("/principal")
    public ResponseEntity<PrincipalDto> getPrincipal(Authentication authentication) {
        UserDetails principal = (UserDetails) authentication.getPrincipal();

        PrincipalDto dto = new PrincipalDto(principal.getUsername(), principal.getAuthorities());

        return ResponseEntity.ok(dto);
    }

    @PostMapping
    public ResponseEntity<String> createUser(@RequestBody UserDto userDto) {
        try {
            userService.registerUser(userDto);
            return ResponseEntity.status(HttpStatus.CREATED).build();

        } catch (Exception e) {
            if (e instanceof DuplicateKeyException) {
                return ResponseEntity.status(HttpStatus.CONFLICT).build();
            }

            if (e instanceof IllegalArgumentException) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

}
