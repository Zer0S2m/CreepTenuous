package com.zer0s2m.creeptenuous.security.jwt.domain;

public record JwtRedisData(
        String login,
        String accessToken,
        String refreshToken
) { }
