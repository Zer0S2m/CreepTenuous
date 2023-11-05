package com.zer0s2m.creeptenuous.redis.models.base;

import jakarta.persistence.Column;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.index.Indexed;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Base class for defining a file object
 */
@Data
public abstract class IBaseRedis implements BaseRedis {

    @Id
    private String systemName;

    @Column(name = "realName")
    private String realName;

    @Column(name = "path")
    private String path;

    @Indexed
    @Column(name = "login")
    private String login;

    @Column(name = "role")
    private String role;

    @Column(name = "isDirectory")
    private Boolean isDirectory;

    @Column(name = "isFile")
    private Boolean isFile;

    @Column(name = "userLogins")
    private List<String> userLogins;

    @Column(name = "createdAt")
    private LocalDateTime createdAt;

}
