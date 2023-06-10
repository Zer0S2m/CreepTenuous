package com.zer0s2m.creeptenuous.redis.models;

import com.zer0s2m.creeptenuous.redis.models.base.BaseRedis;
import jakarta.persistence.Column;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

import java.util.List;

@Data
@RedisHash("directories")
public class DirectoryRedis implements BaseRedis {
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

    @Column(name = "user_logins")
    private List<String> userLogins;

    public DirectoryRedis(
            String login,
            String role,
            String realNameDirectory,
            String systemNameDirectory,
            String pathDirectory,
            List<String> userLogins
    ) {
        this.login = login;
        this.role = role;
        this.realNameDirectory = realNameDirectory;
        this.systemNameDirectory = systemNameDirectory;
        this.pathDirectory = pathDirectory;
        this.userLogins = userLogins;
        this.isDirectory = true;
        this.isFile = false;
    }
}
