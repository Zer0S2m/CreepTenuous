package com.zer0s2m.creeptenuous.security.jwt.http;

public record JwtResponse(
        String accessToken,
        String refreshToken
) { }
