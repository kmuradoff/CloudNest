package me.kmuradoff.cloudnest.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.RequiredArgsConstructor;
import me.kmuradoff.cloudnest.dto.AuthResponse;
import me.kmuradoff.cloudnest.dto.LoginRequest;
import me.kmuradoff.cloudnest.dto.RegisterRequest;
import me.kmuradoff.cloudnest.exception.InvalidTokenException;
import me.kmuradoff.cloudnest.exception.TokenExpiredException;
import me.kmuradoff.cloudnest.exception.UsernameAlreadyExistsException;
import me.kmuradoff.cloudnest.jpa.model.User;
import me.kmuradoff.cloudnest.jpa.repository.UserRepository;
import me.kmuradoff.cloudnest.mapper.UserMapper;
import me.kmuradoff.cloudnest.util.JwtUtil;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final UserMapper userMapper;

    public AuthResponse login(LoginRequest loginRequest) {
        User user = userRepository.findByUsername(loginRequest.getUsername());
        if (user == null)
            throw new UsernameNotFoundException("User not found");

        boolean passwordMatches = passwordEncoder.matches(loginRequest.getPassword(), user.getPassword());

        if (!passwordMatches) {
            throw new BadCredentialsException("Invalid credentials");
        }

        return new AuthResponse(jwtUtil.generateAccessToken(user),
                                jwtUtil.generateRefreshToken(user)
        );
    }

    public AuthResponse register(RegisterRequest registerRequest) {
        User user = userRepository.findByUsername(registerRequest.getUsername());
        if (user != null)
            throw new UsernameAlreadyExistsException("Username already exists");

        User newUser = userMapper.registerToUser(registerRequest);
        newUser.setPassword(passwordEncoder.encode(registerRequest.getPassword()));

        User savedUser = userRepository.save(newUser);

        return new AuthResponse(jwtUtil.generateAccessToken(savedUser),
                                jwtUtil.generateRefreshToken(savedUser)
        );
    }

    public AuthResponse refreshToken(String refreshToken) {
        Claims claims;

        try {
            claims = jwtUtil.validateJwt(refreshToken).getPayload();
        } catch (ExpiredJwtException e) {
            throw new TokenExpiredException("Refresh token expired");
        }

        if (!claims.get("typ", String.class).equals("refresh")) {
            throw new InvalidTokenException("Invalid refresh token");
        }

        UUID userUuid = UUID.fromString(claims.get("uuid", String.class));
        User user = userRepository.findById(userUuid)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return new AuthResponse(
                jwtUtil.generateAccessToken(user),
                jwtUtil.generateRefreshToken(user)
        );
    }
}
