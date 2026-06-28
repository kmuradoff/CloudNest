package me.kmuradoff.cloudnest.controller;


import lombok.RequiredArgsConstructor;
import me.kmuradoff.cloudnest.dto.AuthResponse;
import me.kmuradoff.cloudnest.dto.LoginRequest;
import me.kmuradoff.cloudnest.dto.RegisterRequest;
import me.kmuradoff.cloudnest.service.AuthService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/login")
    public AuthResponse login(@RequestBody LoginRequest loginRequest) {
        return authService.login(loginRequest);
    }

    @PostMapping("/register")
    public AuthResponse register(@RequestBody RegisterRequest registerRequest) {
        return authService.register(registerRequest);
    }

    @GetMapping("/testing")
    public String testing() {
        return "Authenticated";
    }
}
