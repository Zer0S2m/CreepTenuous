package com.zer0s2m.creeptenuous.redis.models;

import com.zer0s2m.creeptenuous.redis.models.base.IBaseRedis;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.redis.core.RedisHash;

import java.time.LocalDateTime;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@RedisHash("files")
public class FileRedis extends IBaseRedis {

    public FileRedis() {}

    public FileRedis(
            String login,
            String role,
            String realName,
            String systemName,
            String path,
            List<String> userLogins
    ) {
        setLogin(login);
        setRole(role);
        setRealName(realName);
        setSystemName(systemName);
        setPath(path);
        setUserLogins(userLogins);
        setIsDirectory(false);
        setIsFile(true);
        setCreatedAt(LocalDateTime.now());
    }

}
