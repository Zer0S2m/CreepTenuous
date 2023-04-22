package com.zer0s2m.CreepTenuous.providers.jwt.utils;

import com.zer0s2m.CreepTenuous.providers.jwt.domain.JwtAuthentication;
import com.zer0s2m.CreepTenuous.services.user.enums.UserRole;

import io.jsonwebtoken.Claims;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Set;
import java.util.TreeSet;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class JwtUtils {
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
}
