package me.kmuradoff.cloudnest.controller;


import lombok.RequiredArgsConstructor;
import me.kmuradoff.cloudnest.dto.AuthResponse;
import me.kmuradoff.cloudnest.dto.LoginRequest;
import me.kmuradoff.cloudnest.dto.RegisterRequest;
import me.kmuradoff.cloudnest.service.AuthService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/login")
    public AuthResponse login(@RequestBody LoginRequest loginRequest, @RequestHeader("X-Device-Id") String deviceId) {
        return authService.login(loginRequest, deviceId);
    }

    @PostMapping("/register")
    public AuthResponse register(@RequestBody RegisterRequest registerRequest, @RequestHeader("X-Device-Id") String deviceId) {
        return authService.register(registerRequest, deviceId);
    }

    @PostMapping("/refresh")
    public AuthResponse refreshToken(String refreshToken, @RequestHeader("X-Device-Id") String deviceId) {
        return authService.refreshTokens(refreshToken, deviceId);
    }

    @PostMapping("/logout")
    public void logout(Authentication authentication,
                             @RequestParam(defaultValue = "false") boolean allDevices,
                             @RequestHeader("X-Device-Id") String deviceId) {
        String userId = authentication.getPrincipal().toString();

        authService.logout(UUID.fromString(userId), allDevices, deviceId);
    }

    @GetMapping("/testing")
    public String testing() {
        return "Testing OK!";
    }
}
