package CreepTenuous.providers.jwt;

import CreepTenuous.providers.jwt.http.JwtUserRequest;

import CreepTenuous.services.user.enums.UserRole;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;
import javax.crypto.SecretKey;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.Jwts;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.security.Key;

@Component
public class JwtProvider {
    private final Integer validityInMsAccess = 60 * 10;
    private final Integer validityInDaysRefresh = 30;
    private final SecretKey jwtAccessSecret;
    private final SecretKey jwtRefreshSecret;

    public JwtProvider(
            @Value("${jwt.secret.access}") String jwtAccessSecret,
            @Value("${jwt.secret.refresh}") String jwtRefreshSecret
    ) {
        this.jwtAccessSecret = Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtAccessSecret));
        this.jwtRefreshSecret = Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtRefreshSecret));
    }

    @NonNull
    public String generateAccessToken(@NonNull JwtUserRequest user, @NonNull UserRole role) {
        final LocalDateTime now = LocalDateTime.now();
        final Instant accessExpirationInstant =
                now.plusMinutes(validityInMsAccess).atZone(ZoneId.systemDefault()).toInstant();
        final Date accessExpiration = Date.from(accessExpirationInstant);
        return Jwts.builder()
                .setSubject(user.getLogin())
                .setExpiration(accessExpiration)
                .signWith(jwtAccessSecret)
                .claim("role", role)
                .claim("login", user.getLogin())
                .compact();
    }

    @NonNull
    public String generateRefreshToken(@NonNull JwtUserRequest user) {
        final LocalDateTime now = LocalDateTime.now();
        final Instant refreshExpirationInstant =
                now.plusDays(validityInDaysRefresh).atZone(ZoneId.systemDefault()).toInstant();
        final Date refreshExpiration = Date.from(refreshExpirationInstant);
        return Jwts.builder()
                .setSubject(user.getLogin())
                .setExpiration(refreshExpiration)
                .signWith(jwtRefreshSecret)
                .compact();
    }

    @NonNull
    public Boolean validateAccessToken(@NonNull String accessToken) {
        return validateToken(accessToken, jwtAccessSecret);
    }

    @NonNull
    public Boolean validateRefreshToken(@NonNull String refreshToken) {
        return validateToken(refreshToken, jwtRefreshSecret);
    }

    @NonNull
    private boolean validateToken(@NonNull String token, @NonNull Key secret) {
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
}
