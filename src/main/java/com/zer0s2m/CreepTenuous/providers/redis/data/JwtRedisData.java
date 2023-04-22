package com.zer0s2m.CreepTenuous.providers.redis.data;

public record JwtRedisData(
        String login,
        String accessToken,
        String refreshToken
) {
}
