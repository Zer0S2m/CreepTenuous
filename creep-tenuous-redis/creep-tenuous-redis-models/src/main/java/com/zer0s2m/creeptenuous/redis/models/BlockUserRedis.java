package com.zer0s2m.creeptenuous.redis.models;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

@Data
@RedisHash("block-users")
public class BlockUserRedis {

    @Id
    private String login;

    @TimeToLive
    private Long expiration;

    public BlockUserRedis(String login, Long expiration) {
        this.login = login;
        this.expiration = expiration;
    }

}
