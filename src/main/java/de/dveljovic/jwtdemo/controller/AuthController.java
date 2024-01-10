package de.dveljovic.jwtdemo.controller;

import de.dveljovic.jwtdemo.auth.JwtUtil;
import de.dveljovic.jwtdemo.model.User;
import de.dveljovic.jwtdemo.model.request.LoginRequest;
import de.dveljovic.jwtdemo.model.response.ErrorResponse;
import de.dveljovic.jwtdemo.model.response.LoginResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/rest/auth")
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    public AuthController(final AuthenticationManager authenticationManager,
                          final JwtUtil jwtUtil) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/login")
    public ResponseEntity login(final @RequestBody LoginRequest loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.email(), loginRequest.password()));
            final String email = authentication.getName();
            final User user = new User(email, "", "", "");
            final String token = jwtUtil.createToken(user);
            final LoginResponse loginResponse = new LoginResponse(email, token);
            return ResponseEntity.ok(loginResponse);
        } catch (BadCredentialsException exception) {
            final ErrorResponse errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST, "Invalid username");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        } catch (Exception exception) {
            final ErrorResponse errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST, exception.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }
}
