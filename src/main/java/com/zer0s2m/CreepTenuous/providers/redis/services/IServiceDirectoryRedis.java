package com.zer0s2m.CreepTenuous.providers.redis.services;

import com.zer0s2m.CreepTenuous.providers.redis.models.DirectoryRedis;
import com.zer0s2m.CreepTenuous.providers.redis.repositories.DirectoryRedisRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public interface IServiceDirectoryRedis {
    static DirectoryRedis createObj(String login, String role, String nameDirectory, String pathDirectory) {
        return new DirectoryRedis(login, role, nameDirectory, pathDirectory);
    }

    void push(DirectoryRedis objRedis);

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
}
