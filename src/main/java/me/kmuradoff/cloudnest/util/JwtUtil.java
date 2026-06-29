package me.kmuradoff.cloudnest.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import me.kmuradoff.cloudnest.config.JwtProperties;
import me.kmuradoff.cloudnest.jpa.model.User;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

@Component
@RequiredArgsConstructor
public class JwtUtil {

    private final JwtProperties jwtProperties;

    public String generateAccessToken(User user) {
        return Jwts.builder()
                .claim("uuid", user.getUuid())
                .claim("typ", "access")
                .issuer("CloudNest")
                .issuedAt(Date.from(Instant.now()))
                .expiration(Date.from(Instant.now().plus(jwtProperties.getAccessExpiration(), ChronoUnit.MINUTES)))
                .signWith(Keys.hmacShaKeyFor(jwtProperties.getSecret().getBytes()))
                .compact();
    }

    public String generateRefreshToken(User user) {
        return Jwts.builder()
                .claim("uuid", user.getUuid())
                .claim("typ", "refresh")
                .issuer("CloudNest")
                .issuedAt(Date.from(Instant.now()))
                .expiration(Date.from(Instant.now().plus(jwtProperties.getRefreshExpiration(), ChronoUnit.DAYS)))
                .signWith(Keys.hmacShaKeyFor(jwtProperties.getSecret().getBytes()))
                .compact();
    }

    public Jws<Claims> validateJwt(@NonNull String token) {
        return Jwts.parser()
                .verifyWith(Keys.hmacShaKeyFor(jwtProperties.getSecret().getBytes()))
                .build()
                .parseSignedClaims(token);
    }

    public Instant getExpiration(String token) {
        Claims claims = validateJwt(token).getPayload();
        return claims.getExpiration().toInstant();
    }
}
