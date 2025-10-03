package com.artshop.backend.controllers;

import com.artshop.backend.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    @Value("${app.security.admin.username:ADMIN_USER}")
    private String adminUsername;

    @Value("${app.security.admin.password-hash:ADMIN_PASSWORD_HASH}")
    private String adminPasswordHash;

    private final JwtService jwt;
    private final PasswordEncoder encoder;

    public record LoginRequest(String username, String password) {
    }

    public record LoginResponse(String token) {
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest req) {
        if (!adminUsername.equals(req.username()) || !encoder.matches(req.password(), adminPasswordHash)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "INVALID_CREDENTIALS"));
        }
        var token = jwt.generateToken(adminUsername, List.of(new SimpleGrantedAuthority("ROLE_ADMIN")));
        return ResponseEntity.ok(new LoginResponse(token));
    }
}