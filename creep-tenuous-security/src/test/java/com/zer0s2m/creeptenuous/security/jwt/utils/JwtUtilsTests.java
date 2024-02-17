package com.zer0s2m.creeptenuous.security.jwt.utils;

import com.zer0s2m.creeptenuous.common.enums.UserRole;
import com.zer0s2m.creeptenuous.security.jwt.JwtUtils;
import com.zer0s2m.creeptenuous.starter.test.annotations.TestTagService;
import com.zer0s2m.creeptenuous.starter.test.helpers.UtilsAuthAction;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.impl.DefaultClaims;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

@ExtendWith(MockitoExtension.class)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@TestTagService
public class JwtUtilsTests {

    @Test
    public void generate_success() {
        final Map<String, Object> payload = new HashMap<>();
        payload.put(Claims.SUBJECT, "subject");
        payload.put("name", "name");
        payload.put("role", UserRole.ROLE_USER.getAuthority());

        final Claims claims = new DefaultClaims(payload);

        Assertions.assertDoesNotThrow(
                () -> JwtUtils.generate(claims)
        );
    }

    @Test
    public void getPureAccessToken_success_rawToken() {
        final String cleanToken = JwtUtils.getPureAccessToken(
                UtilsAuthAction.builderHeader(UtilsAuthAction.generateAccessToken()));

        Assertions.assertFalse(
                StringUtils.hasText(cleanToken) && cleanToken.startsWith(JwtUtils.HEADER_PREFIX));
    }

    @Test
    public void getPureAccessToken_success_cleanToken() {
        final String cleanToken = JwtUtils.getPureAccessToken(UtilsAuthAction.generateAccessToken());

        Assertions.assertFalse(
                StringUtils.hasText(cleanToken) && cleanToken.startsWith(JwtUtils.HEADER_PREFIX));
    }

}
