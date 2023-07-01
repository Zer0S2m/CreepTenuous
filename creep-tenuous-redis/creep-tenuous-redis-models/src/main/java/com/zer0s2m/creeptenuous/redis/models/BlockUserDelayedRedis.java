package com.zer0s2m.creeptenuous.redis.models;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

@Data
@RedisHash("block-user-period-times")
public class BlockUserDelayedRedis {

    @Id
    private String login;

    @TimeToLive
    private Long expiration;

    public BlockUserDelayedRedis(String login, Long expiration) {
        this.login = login;
        this.expiration = expiration;
    }

}
