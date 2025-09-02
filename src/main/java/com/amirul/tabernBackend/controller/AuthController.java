package com.amirul.tabernBackend.controller;

import com.amirul.tabernBackend.dtos.JwtResponseDTO;
import com.amirul.tabernBackend.dtos.LoginRequestDTO;
import com.amirul.tabernBackend.dtos.SignupRequestDTO;
import com.amirul.tabernBackend.model.User;
import com.amirul.tabernBackend.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@RequestBody SignupRequestDTO signupRequest) {
        try {
            if (authService.emailExists(signupRequest.getEmail())) {
                return ResponseEntity
                        .badRequest()
                        .body(Map.of("message", "Error : Email is already in use!"));
            }
            if (authService.usernameExists(signupRequest.getUsername())) {
                return ResponseEntity
                        .badRequest()
                        .body(Map.of("message", "Error : Username is already taken!"));
            }

            User user = authService.registerUser(signupRequest);

            return ResponseEntity.ok(Map.of(
                    "message", "User registered successfully",
                    "userId", user.getId()
            ));

        } catch (Exception e) {
            return ResponseEntity
                    .badRequest()
                    .body(Map.of("message", "Error : " + e.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequestDTO loginRequest) {
        try {
            JwtResponseDTO jwtResponse = authService.authenticateUser(loginRequest);

            return ResponseEntity.ok(jwtResponse);
        } catch (BadCredentialsException e) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Error : Invalid email or password"));
        } catch (Exception e) {
            return ResponseEntity
                    .badRequest()
                    .body(Map.of("message", "Error : " + e.getMessage()));
        }
    }

    @GetMapping("/check-email")
    public ResponseEntity<?> checkEmailExists(@RequestParam String email) {
        boolean exists = authService.emailExists(email);
        return ResponseEntity.ok(Map.of("exists", exists));
    }

    @GetMapping("/check-username")
    public ResponseEntity<?> checkUsernameExists(@RequestParam String username) {
        boolean exists = authService.usernameExists(username);
        return ResponseEntity.ok(Map.of("exists", exists));
    }
}
