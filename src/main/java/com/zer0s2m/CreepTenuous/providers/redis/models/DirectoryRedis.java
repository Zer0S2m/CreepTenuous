package com.zer0s2m.CreepTenuous.providers.redis.models;

import jakarta.persistence.Column;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

@Data
@RedisHash("directories")
public class DirectoryRedis {
    @Id
    private String nameDirectory;

    @Column(name = "pathDirectory")
    private String pathDirectory;

    @Indexed
    @Column(name = "login")
    private String login;

    @Column(name = "role")
    private String role;

    @Column(name = "isDirectory")
    private Boolean isDirectory;

    @Column(name = "isFile")
    private Boolean isFile;

    public DirectoryRedis(String login, String role, String nameDirectory, String pathDirectory) {
        this.login = login;
        this.role = role;
        this.nameDirectory = nameDirectory;
        this.pathDirectory = pathDirectory;
        this.isDirectory = true;
        this.isFile = false;
    }
}
