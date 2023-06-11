package com.zer0s2m.creeptenuous.security.jwt.utils;

import com.zer0s2m.creeptenuous.common.enums.UserRole;
import com.zer0s2m.creeptenuous.security.jwt.domain.JwtAuthentication;
import io.jsonwebtoken.Claims;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.util.StringUtils;

import java.util.Set;
import java.util.TreeSet;

/**
 * Helper class for serving resources related to JWT tokens
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class JwtUtils {

    public static final String HEADER_PREFIX = "Bearer ";

    /**
     * Get a view token for authorization
     * @param claims JWT token claims
     * @return token for authorization
     */
    public static @NotNull JwtAuthentication generate(Claims claims) {
        final JwtAuthentication jwtInfoToken = new JwtAuthentication();
        jwtInfoToken.setRoles(getRoles(claims));
        jwtInfoToken.setName(claims.get("name", String.class));
        jwtInfoToken.setLogin(claims.getSubject());
        return jwtInfoToken;
    }

    /**
     * Get user roles from JWT token claims
     * @param claims JWT token claims
     * @return user roles
     */
    private static @NotNull Set<UserRole> getRoles(@NotNull Claims claims) {
        Set<UserRole> roles = new TreeSet<>();
        roles.add(UserRole.valueOf(claims.get("role", String.class)));
        return roles;
    }

    /**
     * Get clean JWT token from dirty header
     * @param token header token
     * @return JWT token
     */
    public static String getPureAccessToken(String token) {
        if (StringUtils.hasText(token) && token.startsWith(JwtUtils.HEADER_PREFIX)) {
            return token.substring(7);
        }
        return token;
    }

}
