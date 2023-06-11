package com.zer0s2m.creeptenuous.security.jwt.providers;

import com.zer0s2m.creeptenuous.common.enums.UserRole;
import com.zer0s2m.creeptenuous.security.jwt.http.JwtUserRequest;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import javax.crypto.SecretKey;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.Jwts;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.security.Key;

/**
 * Provider for the generation and verification of JWT tokens
 */
@Service("jwt-provider")
public class JwtProvider {

    private final SecretKey jwtAccessSecret;

    private final SecretKey jwtRefreshSecret;

    public JwtProvider(
            final @Value("${jwt.secret.access}") @NotNull String jwtAccessSecret,
            final @Value("${jwt.secret.refresh}") @NotNull String jwtRefreshSecret
    ) {
        this.jwtAccessSecret = Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtAccessSecret.trim()));
        this.jwtRefreshSecret = Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtRefreshSecret.trim()));
    }

    /**
     * Get to generate a JWT access token
     * @param user user data
     * @param role user role
     * @return JWT access token
     */
    public String generateAccessToken(@NotNull JwtUserRequest user, @NotNull UserRole role) {
        final LocalDateTime now = LocalDateTime.now();
        final int validityInMsAccess = 10 * 10;
        final Instant accessExpirationInstant = now
                .plusMinutes(validityInMsAccess)
                .atZone(ZoneId.systemDefault())
                .toInstant();
        final Date accessExpiration = Date.from(accessExpirationInstant);
        return Jwts.builder()
                .setSubject(user.login())
                .setExpiration(accessExpiration)
                .signWith(jwtAccessSecret)
                .claim("role", role.getAuthority())
                .claim("login", user.login())
                .compact();
    }

    /**
     * Get to generate a JWT refresh token
     * @param user user data
     * @return JWT refresh token
     */
    public String generateRefreshToken(@NotNull JwtUserRequest user) {
        final LocalDateTime now = LocalDateTime.now();
        final int validityInDaysRefresh = 30;
        final Instant refreshExpirationInstant = now
                .plusDays(validityInDaysRefresh)
                .atZone(ZoneId.systemDefault())
                .toInstant();
        final Date refreshExpiration = Date.from(refreshExpirationInstant);
        return Jwts.builder()
                .setSubject(user.login())
                .setExpiration(refreshExpiration)
                .signWith(jwtRefreshSecret)
                .compact();
    }

    /**
     * Validation for JWT access token
     * @param accessToken JWT access token
     * @return is it valid
     */
    public Boolean validateAccessToken(String accessToken) {
        return validateToken(accessToken, jwtAccessSecret);
    }

    /**
     * Validation for JWT refresh token
     * @param refreshToken JWT refresh token
     * @return is it valid
     */
    public Boolean validateRefreshToken(String refreshToken) {
        return validateToken(refreshToken, jwtRefreshSecret);
    }

    /**
     * Validation for JWT token
     * @param token JWT token
     * @param secret the secret key
     * @return is it valid
     */
    private boolean validateToken(String token, Key secret) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(secret)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Get JWT access token claims
     * @param token JWT access token
     * @return JWT token claims
     */
    public Claims getAccessClaims(String token) {
        return getClaims(token, jwtAccessSecret);
    }

    /**
     * Get JWT refresh token claims
     * @param token JWT refresh token
     * @return JWT token claims
     */
    public Claims getRefreshClaims(String token) {
        return getClaims(token, jwtRefreshSecret);
    }

    /**
     * JWT token claims
     * @param token JWT token
     * @param secret the secret key
     * @return JWT token claims
     */
    private Claims getClaims(String token, Key secret) {
        return Jwts.parserBuilder()
                .setSigningKey(secret)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

}
