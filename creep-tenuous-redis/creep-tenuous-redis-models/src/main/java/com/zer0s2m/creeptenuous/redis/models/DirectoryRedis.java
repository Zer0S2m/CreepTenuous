package com.zer0s2m.creeptenuous.redis.models;

import com.zer0s2m.creeptenuous.redis.models.base.IBaseRedis;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.redis.core.RedisHash;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@RedisHash("directories")
public class DirectoryRedis extends IBaseRedis {

    public DirectoryRedis() {}

    public DirectoryRedis(
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
        setIsDirectory(true);
        setIsFile(false);
    }

}

