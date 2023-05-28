package com.zer0s2m.creeptenuous.security.jwt.utils;

import com.zer0s2m.creeptenuous.common.enums.UserRole;
import com.zer0s2m.creeptenuous.security.jwt.domain.JwtAuthentication;
import io.jsonwebtoken.Claims;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.util.StringUtils;

import java.util.Set;
import java.util.TreeSet;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class JwtUtils {
    public static final String HEADER_PREFIX = "Bearer ";

    public static JwtAuthentication generate(Claims claims) {
        final JwtAuthentication jwtInfoToken = new JwtAuthentication();
        jwtInfoToken.setRoles(getRoles(claims));
        jwtInfoToken.setName(claims.get("name", String.class));
        jwtInfoToken.setLogin(claims.getSubject());
        return jwtInfoToken;
    }

    private static Set<UserRole> getRoles(Claims claims) {
        Set<UserRole> roles = new TreeSet<>();
        roles.add(UserRole.valueOf(claims.get("role", String.class)));
        return roles;
    }

    public static String getPureAccessToken(String token) {
        if (StringUtils.hasText(token) && token.startsWith(JwtUtils.HEADER_PREFIX)) {
            return token.substring(7);
        }
        return token;
    }
}
