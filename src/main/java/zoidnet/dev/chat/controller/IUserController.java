package zoidnet.dev.chat.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RequestBody;
import zoidnet.dev.chat.model.dto.PrincipalDto;
import zoidnet.dev.chat.model.dto.UserDto;


@Tag(name = "Users")
public interface IUserController {

    @Operation(summary = "Login")
    @ApiResponse(responseCode = "200", description = "Success")
    @ApiResponse(responseCode = "401", description = "Bad credentials", content = @Content)
    @ApiResponse(responseCode = "403", description = "Invalid CSRF token", content = @Content)
    ResponseEntity<PrincipalDto> login(@RequestBody UserDto userDto);

    @Operation(summary = "Logout")
    @ApiResponse(responseCode = "200", description = "Success", content = @Content)
    @ApiResponse(responseCode = "403", description = "Invalid CSRF token", content = @Content)
    void logout();

    @Operation(summary = "Get logged in user")
    @SecurityRequirement(name = "Authentication token")
    @ApiResponse(responseCode = "200", description = "Success")
    @ApiResponse(responseCode = "401", description = "Unauthenticated", content = @Content)
    ResponseEntity<PrincipalDto> getPrincipal(Authentication authentication);

    @Operation(summary = "Create a new user")
    @ApiResponse(responseCode = "201", description = "Created", content = @Content)
    @ApiResponse(responseCode = "400", description = "Bad request", content = @Content)
    @ApiResponse(responseCode = "403", description = "Invalid CSRF token", content = @Content)
    @ApiResponse(responseCode = "409", description = "Username already exists", content = @Content)
    @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    ResponseEntity<String> createUser(@RequestBody UserDto userDto);
}
