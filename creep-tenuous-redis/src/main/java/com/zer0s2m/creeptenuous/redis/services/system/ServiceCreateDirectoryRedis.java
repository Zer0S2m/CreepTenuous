package com.zer0s2m.creeptenuous.redis.services.system;

import com.zer0s2m.creeptenuous.redis.models.DirectoryRedis;
import com.zer0s2m.creeptenuous.redis.services.system.base.BaseServiceRedis;
import com.zer0s2m.creeptenuous.redis.repositories.DirectoryRedisRepository;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public interface ServiceCreateDirectoryRedis extends BaseServiceRedis<DirectoryRedis> {
    static DirectoryRedis getObjRedis(
            String login,
            String role,
            String realNameDirectory,
            String systemNameDirectory,
            String pathDirectory
    ) {
        return new DirectoryRedis(login, role, realNameDirectory, systemNameDirectory, pathDirectory);
    }

    static List<DirectoryRedis> getDirectoriesByLogin(
            DirectoryRedisRepository repository,
            String login,
            List<String> namesDirectory
    ) {
        List<DirectoryRedis> data = new ArrayList<>();
        Iterable<DirectoryRedis> objects = repository.findAllById(namesDirectory);
        objects.forEach((obj) -> {
            if (Objects.equals(obj.getLogin(), login)) {
                data.add(obj);
            }
        });
        return data;
    }

    void checkIsExistsDirectory(Path systemSource, String nameDirectory);
}
