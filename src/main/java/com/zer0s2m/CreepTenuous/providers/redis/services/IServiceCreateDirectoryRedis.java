package com.zer0s2m.CreepTenuous.providers.redis.services;

import com.zer0s2m.CreepTenuous.providers.redis.models.DirectoryRedis;
import com.zer0s2m.CreepTenuous.providers.redis.repositories.DirectoryRedisRepository;
import com.zer0s2m.CreepTenuous.providers.redis.services.base.IBaseServiceRedis;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public interface IServiceCreateDirectoryRedis extends IBaseServiceRedis<DirectoryRedis> {
    static DirectoryRedis getObjRedis(String login, String role, String nameDirectory, String pathDirectory) {
        return new DirectoryRedis(login, role, nameDirectory, pathDirectory);
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
}
