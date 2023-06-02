package com.zer0s2m.creeptenuous.redis.models;

import com.zer0s2m.creeptenuous.common.enums.OperationRights;
import jakarta.persistence.Column;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

import java.util.List;

@Data
@RedisHash("user_rights")
public class RightUserFileSystemObjectRedis {

    @Id
    private String fileSystemObject;

    @Indexed
    @Column(name = "login")
    private String login;

    @Indexed
    @Column(name = "right")
    private List<OperationRights> right;

    public RightUserFileSystemObjectRedis(String fileSystemObject, String login, List<OperationRights> right) {
        this.fileSystemObject = fileSystemObject;
        this.login = login;
        this.right = right;
    }
}
