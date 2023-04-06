package CreepTenuous.providers.redis.data;

public record JwtRedisData(
        String login,
        String accessToken,
        String refreshToken
) {
}
