package com.zer0s2m.creeptenuous.redis.models;

import jakarta.persistence.Column;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

@Data
@RedisHash("directories")
public class DirectoryRedis {
    @Indexed
    @Column(name = "realNameDirectory")
    private String realNameDirectory;

    @Id
    private String systemNameDirectory;

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

    public DirectoryRedis(
            String login,
            String role,
            String realNameDirectory,
            String systemNameDirectory,
            String pathDirectory
    ) {
        this.login = login;
        this.role = role;
        this.realNameDirectory = realNameDirectory;
        this.systemNameDirectory = systemNameDirectory;
        this.pathDirectory = pathDirectory;
        this.isDirectory = true;
        this.isFile = false;
    }
}
