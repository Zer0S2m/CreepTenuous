package com.zer0s2m.CreepTenuous.providers.redis.models;

import jakarta.persistence.Column;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

@Data
@RedisHash("files")
public class FileRedis {
    @Id
    private String nameFile;

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

    public FileRedis(String login, String role, String nameFile, String pathFile) {
        this.login = login;
        this.role = role;
        this.nameFile = nameFile;
        this.pathFile = pathFile;
        this.isDirectory = false;
        this.isFile = true;
    }
}
