package me.kmuradoff.cloudnest.controller;


import lombok.RequiredArgsConstructor;
import me.kmuradoff.cloudnest.dto.AuthResponse;
import me.kmuradoff.cloudnest.dto.LoginRequest;
import me.kmuradoff.cloudnest.dto.RegisterRequest;
import me.kmuradoff.cloudnest.service.AuthService;
import me.kmuradoff.cloudnest.service.UserDetailsImpl;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

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
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        authService.logout(userDetails.getUuid(), allDevices, deviceId);
    }
}
