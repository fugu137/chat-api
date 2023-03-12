package zoidnet.dev.chat.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.commons.lang3.NotImplementedException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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
@Tag(name = "Users")
public class UserController {

    private final UserService userService;


    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping(path = "/login", consumes = {MediaType.APPLICATION_FORM_URLENCODED_VALUE})
    @Operation(summary = "Login")
    @ApiResponse(responseCode = "200", description = "Success")
    @ApiResponse(responseCode = "401", description = "Bad credentials", content = @Content)
    @ApiResponse(responseCode = "403", description = "Invalid csrf token", content = @Content)
    public ResponseEntity<PrincipalDto> login(@RequestBody UserDto userDto) {
        throw new NotImplementedException("This method should not be called. It is implemented by Spring Security filters.");
    }

    @PostMapping("/logout")
    @Operation(summary = "Logout")
    @ApiResponse(responseCode = "200", description = "Success", content = @Content)
    @ApiResponse(responseCode = "403", description = "Invalid csrf token", content = @Content)
    public void logout() {
        throw new NotImplementedException("This method should not be called. It is implemented by Spring Security filters.");
    }

    @GetMapping("/principal")
    @Operation(summary = "Get logged in user")
    @ApiResponse(responseCode = "200", description = "Success")
    @ApiResponse(responseCode = "401", description = "Invalid credentials", content = @Content)
    public ResponseEntity<PrincipalDto> getPrincipal(Authentication authentication) {
        UserDetails principal = (UserDetails) authentication.getPrincipal();
        PrincipalDto dto = new PrincipalDto(principal.getUsername(), principal.getAuthorities());

        return ResponseEntity.ok(dto);
    }

    @PostMapping
    @Operation(summary = "Create a new user")
    @ApiResponse(responseCode = "201", description = "Created", content = @Content)
    @ApiResponse(responseCode = "400", description = "Bad request", content = @Content)
    @ApiResponse(responseCode = "403", description = "Invalid csrf token", content = @Content)
    @ApiResponse(responseCode = "409", description = "Username already exists", content = @Content)
    @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    public ResponseEntity<String> createUser(@RequestBody UserDto userDto) {
        try {
            User registeredUser = userService.registerUser(userDto);

            if (registeredUser == null) return ResponseEntity.status(HttpStatus.CONFLICT).build();

            return ResponseEntity.status(HttpStatus.CREATED).build();

        } catch (Exception e) {
            if (e instanceof IllegalArgumentException) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

}
