package com.zer0s2m.creeptenuous.starter.test.helpers;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.security.Key;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

/**
 * Class for generating JWT tokens
 */
public interface UtilsAuthAction {

    String ROLE_USER = "ROLE_USER";
    String LOGIN = "test_login";

    Key SECRET = Keys.hmacShaKeyFor(Decoders.BASE64.decode(
            "EnmtYa94dFCsSAc8iqMeR0JXPjNBrSydu6T02QOG4VxhpLv3DAZoU7fzHbIlK5wc1gkW"
    ));

    /**
     * Generate JWT access token
     * @return JWT access token
     */
    static String generateAccessToken() {
        final Instant accessExpirationInstant = LocalDateTime.now()
                .plusDays(1)
                .atZone(ZoneId.systemDefault())
                .toInstant();
        return Jwts.builder()
                .setSubject(LOGIN)
                .setExpiration(Date.from(accessExpirationInstant))
                .signWith(SECRET)
                .claim("role", ROLE_USER)
                .claim("login", LOGIN)
                .compact();
    }

    /**
     * Collect end token for header
     * @param accessToken JWT access token
     * @return end token for header
     */
    @Contract(pure = true)
    static @NotNull String builderHeader(String accessToken) {
        return "Bearer " + accessToken;
    }

}
