package com.zer0s2m.creeptenuous.security.jwt;

public record JwtRedisData(

        String login,

        String accessToken,

        String refreshToken

) {
}
