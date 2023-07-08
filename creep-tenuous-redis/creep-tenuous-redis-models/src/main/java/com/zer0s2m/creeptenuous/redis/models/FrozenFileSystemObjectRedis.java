package com.zer0s2m.creeptenuous.redis.models;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@Data
@RedisHash("frozen-file-object")
public class FrozenFileSystemObjectRedis {

    @Id
    private String systemName;

    public FrozenFileSystemObjectRedis(String systemName) {
        this.systemName = systemName;
    }

}
