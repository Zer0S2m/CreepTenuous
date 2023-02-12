package CreepTenuous.providers.redis.domain;

import lombok.Data;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;

@Data
@RedisHash("jwt-tokens")
public class JwtRedisData implements Serializable {
    private String accessToken;
    private String refreshToken;
}
