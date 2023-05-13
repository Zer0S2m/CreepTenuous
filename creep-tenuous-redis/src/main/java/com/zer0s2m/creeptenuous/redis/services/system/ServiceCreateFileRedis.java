package com.zer0s2m.creeptenuous.redis.services.system;

import com.zer0s2m.creeptenuous.common.containers.ContainerDataCreateFile;
import com.zer0s2m.creeptenuous.redis.models.FileRedis;
import com.zer0s2m.creeptenuous.redis.services.system.base.BaseServiceRedis;
import com.zer0s2m.creeptenuous.redis.repositories.FileRedisRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public interface ServiceCreateFileRedis extends BaseServiceRedis<FileRedis> {
    static FileRedis getObjRedis(
            String login,
            String role,
            String realNameFile,
            String systemNameFile,
            String pathFile
    ) {
        return new FileRedis(login, role, realNameFile, systemNameFile, pathFile);
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

    void create(ContainerDataCreateFile dataCreatedFile);
}
