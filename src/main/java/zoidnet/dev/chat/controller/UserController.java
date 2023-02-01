package zoidnet.dev.chat.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import zoidnet.dev.chat.controller.dto.PrincipalDto;
import zoidnet.dev.chat.controller.dto.UserDto;
import zoidnet.dev.chat.service.UserService;


@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;


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
            userService.registerUser(userDto);
        } catch (DataAccessException e) {
            if (e instanceof DuplicateKeyException) return ResponseEntity.status(HttpStatus.CONFLICT).build();

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

}
