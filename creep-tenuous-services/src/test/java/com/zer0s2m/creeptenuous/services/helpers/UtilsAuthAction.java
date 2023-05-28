package com.zer0s2m.creeptenuous.services.helpers;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

import java.security.Key;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

public interface UtilsAuthAction {
    String ROLE_USER = User.ROLE_USER.get();
    String LOGIN = User.LOGIN.get();

    Key SECRET = Keys.hmacShaKeyFor(Decoders.BASE64.decode(
            "EnmtYa94dFCsSAc8iqMeR0JXPjNBrSydu6T02QOG4VxhpLv3DAZoU7fzHbIlK5wc1gkW"
    ));

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

    static String builderHeader(String accessToken) {
        return "Bearer " + accessToken;
    }
}
