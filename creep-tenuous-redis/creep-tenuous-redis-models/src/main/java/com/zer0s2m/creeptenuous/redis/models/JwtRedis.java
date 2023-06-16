package com.zer0s2m.creeptenuous.redis.models;

import jakarta.persistence.Column;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;

@Data
@RedisHash("jwt-tokens")
public class JwtRedis implements Serializable {
    @Id
    private String login;

    @Column(name = "accessToken")
    private String accessToken;

    @Column(name = "refreshToken")
    private String refreshToken;

    public JwtRedis(String login, String accessToken, String refreshToken) {
        this.login = login;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }
}
