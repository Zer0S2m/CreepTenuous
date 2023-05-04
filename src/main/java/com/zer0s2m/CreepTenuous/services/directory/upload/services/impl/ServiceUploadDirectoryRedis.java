package com.zer0s2m.CreepTenuous.services.directory.upload.services.impl;

import com.zer0s2m.CreepTenuous.providers.jwt.JwtProvider;
import com.zer0s2m.CreepTenuous.providers.redis.models.DirectoryRedis;
import com.zer0s2m.CreepTenuous.providers.redis.models.FileRedis;
import com.zer0s2m.CreepTenuous.providers.redis.repositories.DirectoryRedisRepository;
import com.zer0s2m.CreepTenuous.providers.redis.repositories.FileRedisRepository;
import com.zer0s2m.CreepTenuous.providers.redis.services.IServiceUploadDirectoryRedis;
import com.zer0s2m.CreepTenuous.services.core.BaseServiceFileSystemRedis;
import com.zer0s2m.CreepTenuous.services.directory.upload.containers.ContainerDataUploadFile;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service("service-upload-directory-redis")
public class ServiceUploadDirectoryRedis extends BaseServiceFileSystemRedis implements IServiceUploadDirectoryRedis {
    private String loginUser;

    private String roleUser;

    @Autowired
    public ServiceUploadDirectoryRedis(
            DirectoryRedisRepository directoryRedisRepository,
            FileRedisRepository fileRedisRepository,
            JwtProvider jwtProvider
    ) {
        super(directoryRedisRepository, fileRedisRepository, jwtProvider);
    }

    @Override
    public void pushDirectoryRedis(List<DirectoryRedis> dataRedis) {
        directoryRedisRepository.saveAll(dataRedis);
    }

    @Override
    public void pushFileRedis(List<FileRedis> dataRedis) {
        fileRedisRepository.saveAll(dataRedis);
    }

    @Override
    public void upload(List<ContainerDataUploadFile> dataUploadFileList) {
        this.loginUser = accessClaims.get("login", String.class);
        this.roleUser = accessClaims.get("role", String.class);

        List<DirectoryRedis> directoryRedisList = new ArrayList<>();
        List<FileRedis> fileRedisList = new ArrayList<>();

        dataUploadFileList.forEach((objRedis) -> {
            if (objRedis.isDirectory()) {
                directoryRedisList.add(buildDirectoryRedis(objRedis));
            } else if (objRedis.isFile()) {
                fileRedisList.add(buildFileRedis(objRedis));
            }
        });

        pushDirectoryRedis(directoryRedisList);
        pushFileRedis(fileRedisList);
    }

    private DirectoryRedis buildDirectoryRedis(ContainerDataUploadFile dataUploadFile) {
        return new DirectoryRedis(
                this.loginUser,
                this.roleUser,
                dataUploadFile.realName(),
                dataUploadFile.systemName(),
                dataUploadFile.systemPath().toString()
        );
    }

    private FileRedis buildFileRedis(ContainerDataUploadFile dataUploadFile) {
        return new FileRedis(
                this.loginUser,
                this.roleUser,
                dataUploadFile.realName(),
                dataUploadFile.systemName(),
                dataUploadFile.systemPath().toString()
        );
    }
}
