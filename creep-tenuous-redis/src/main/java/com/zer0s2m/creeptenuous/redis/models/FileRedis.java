package com.zer0s2m.creeptenuous.redis.models;

import jakarta.persistence.Column;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

@Data
@RedisHash("files")
public class FileRedis {
    @Id
    private String systemNameFile;

    @Indexed
    @Column(name = "realNameFile")
    private String realNameFile;

    @Column(name = "pathFile")
    private String pathFile;

    @Indexed
    @Column(name = "login")
    private String login;

    @Column(name = "role")
    private String role;

    @Column(name = "isDirectory")
    private Boolean isDirectory;

    @Column(name = "isFile")
    private Boolean isFile;

    public FileRedis(String login, String role, String realNameFile, String systemNameFile, String pathFile) {
        this.login = login;
        this.role = role;
        this.realNameFile = realNameFile;
        this.systemNameFile = systemNameFile;
        this.pathFile = pathFile;
        this.isDirectory = false;
        this.isFile = true;
    }
}
