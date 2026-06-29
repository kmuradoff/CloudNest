package me.kmuradoff.cloudnest.service;

import io.jsonwebtoken.Claims;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import me.kmuradoff.cloudnest.dto.AuthResponse;
import me.kmuradoff.cloudnest.dto.LoginRequest;
import me.kmuradoff.cloudnest.dto.RegisterRequest;
import me.kmuradoff.cloudnest.exception.InvalidTokenException;
import me.kmuradoff.cloudnest.exception.UsernameAlreadyExistsException;
import me.kmuradoff.cloudnest.jpa.model.RefreshToken;
import me.kmuradoff.cloudnest.jpa.model.User;
import me.kmuradoff.cloudnest.jpa.repository.RefreshTokenRepository;
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
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final UserMapper userMapper;

    public AuthResponse login(LoginRequest loginRequest, String deviceId) {
        User user = userRepository.findByUsername(loginRequest.getUsername());
        if (user == null)
            throw new UsernameNotFoundException("User not found");

        boolean passwordMatches = passwordEncoder.matches(loginRequest.getPassword(), user.getPassword());

        if (!passwordMatches) {
            throw new BadCredentialsException("Invalid credentials");
        }

        return issueNewTokens(deviceId, user);
    }

    public AuthResponse register(RegisterRequest registerRequest, String deviceId) {
        User user = userRepository.findByUsername(registerRequest.getUsername());
        if (user != null)
            throw new UsernameAlreadyExistsException("Username already exists");

        User newUser = userMapper.registerToUser(registerRequest);
        newUser.setPassword(passwordEncoder.encode(registerRequest.getPassword()));

        User savedUser = userRepository.save(newUser);

        return issueNewTokens(deviceId, savedUser);
    }

    @Transactional
    public AuthResponse refreshTokens(String refreshToken, String deviceId) {
        Claims claims = jwtUtil.validateJwt(refreshToken).getPayload();

        if (!"refresh".equals(claims.get("typ", String.class))) {
            throw new InvalidTokenException("Invalid refresh token");
        }

        UUID userUuid = UUID.fromString(claims.get("uuid", String.class));
        User user = userRepository.findById(userUuid)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return issueNewTokens(deviceId, user);
    }

    public void logout(UUID userUuid, boolean allDevices, String deviceId) {
        if (!allDevices) {
            refreshTokenRepository.deleteAllByDeviceIdAndUser_Uuid(deviceId, userUuid);
            return;
        }

        refreshTokenRepository.deleteAllByUser_Uuid(userUuid);
    }

    private AuthResponse issueNewTokens(String deviceId, User user) {
        refreshTokenRepository.deleteAllByDeviceIdAndUser_Uuid(deviceId, user.getUuid());

        String newRefreshToken = jwtUtil.generateRefreshToken(user);

        refreshTokenRepository.save(RefreshToken.builder()
                .token(newRefreshToken)
                .expiresAt(jwtUtil.getExpiration(newRefreshToken))
                .deviceId(deviceId)
                .user(user)
                .build());

        return new AuthResponse(
                jwtUtil.generateAccessToken(user),
                newRefreshToken
        );
    }
}
