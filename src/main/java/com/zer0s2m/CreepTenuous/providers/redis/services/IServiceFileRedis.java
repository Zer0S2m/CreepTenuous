package com.zer0s2m.CreepTenuous.providers.redis.services;

import com.zer0s2m.CreepTenuous.providers.redis.models.FileRedis;
import com.zer0s2m.CreepTenuous.providers.redis.repositories.FileRedisRepository;
import com.zer0s2m.CreepTenuous.providers.redis.services.base.IBaseServiceRedis;
import com.zer0s2m.CreepTenuous.services.files.create.containers.ContainerDataCreatedFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public interface IServiceFileRedis extends IBaseServiceRedis<FileRedis> {
    static FileRedis getObjRedis(String login, String role, String nameFile, String pathFile) {
        return new FileRedis(login, role, nameFile, pathFile);
    }

    static List<FileRedis> getFilesByLogin(
            FileRedisRepository repository,
            String login,
            List<String> namesFiles
    ) {
        List<FileRedis> data = new ArrayList<>();
        Iterable<FileRedis> objects = repository.findAllById(namesFiles);
        objects.forEach((obj) -> {
            if (Objects.equals(obj.getLogin(), login)) {
                data.add(obj);
            }
        });
        return data;
    }

    void create(ContainerDataCreatedFile dataCreatedFile);
}
