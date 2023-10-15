package com.zer0s2m.creeptenuous.services.redis.system;

import com.zer0s2m.creeptenuous.common.containers.ContainerDataUploadFile;
import com.zer0s2m.creeptenuous.common.containers.ContainerDataUploadFileFragment;
import com.zer0s2m.creeptenuous.redis.models.FileRedis;
import com.zer0s2m.creeptenuous.redis.repository.FileRedisRepository;
import com.zer0s2m.creeptenuous.redis.repository.DirectoryRedisRepository;
import com.zer0s2m.creeptenuous.redis.repository.FrozenFileSystemObjectRedisRepository;
import com.zer0s2m.creeptenuous.redis.services.system.ServiceCreateFileRedis;
import com.zer0s2m.creeptenuous.redis.services.system.ServiceUploadFileRedis;
import com.zer0s2m.creeptenuous.security.jwt.providers.JwtProvider;
import com.zer0s2m.creeptenuous.services.redis.system.base.BaseServiceFileSystemRedisManagerRightsAccessImpl;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for loading file system objects and writing to Redis
 */
@Service("service-upload-file-redis")
public class ServiceUploadFileRedisImpl
        extends BaseServiceFileSystemRedisManagerRightsAccessImpl
        implements ServiceUploadFileRedis {

    @Autowired
    public ServiceUploadFileRedisImpl(
            DirectoryRedisRepository directoryRedisRepository,
            FileRedisRepository fileRedisRepository,
            FrozenFileSystemObjectRedisRepository frozenFileSystemObjectRedisRepository,
            JwtProvider jwtProvider) {
        super(directoryRedisRepository, fileRedisRepository, frozenFileSystemObjectRedisRepository,
                jwtProvider);
    }

    /**
     * Push data in redis create file.
     * @param dataCreatedFile Data upload file.
     * @return Created data.
     */
    @Override
    public FileRedis upload(@NotNull ContainerDataUploadFile dataCreatedFile) {
        String loginUser = accessClaims.get("login", String.class);
        String roleUser = accessClaims.get("role", String.class);

        FileRedis objRedis = new FileRedis(
                loginUser,
                roleUser,
                dataCreatedFile.realNameFile(),
                dataCreatedFile.systemNameFile(),
                dataCreatedFile.systemPathFile().toString(),
                new ArrayList<>());

        push(objRedis);

        return objRedis;
    }

    /**
     * Push data in redis create file.
     * @param dataCreatedFile Data upload files.
     * @return Created data
     */
    @Override
    public Iterable<FileRedis> upload(@NotNull List<ContainerDataUploadFile> dataCreatedFile) {
        String loginUser = accessClaims.get("login", String.class);
        String roleUser = accessClaims.get("role", String.class);

        List<FileRedis> fileRedisList = dataCreatedFile
                .stream()
                .map(obj -> {
                    String systemName = obj.systemNameFile();
                    if (systemName.contains(".")) {
                        String[] splitSystemName = systemName.split("\\.");
                        systemName = splitSystemName[0];
                    }

                    return ServiceCreateFileRedis.getObjRedis(
                            loginUser,
                            roleUser,
                            obj.realNameFile(),
                            systemName,
                            obj.systemPathFile().toString(),
                            new ArrayList<>()
                    );
                })
                .collect(Collectors.toList());

        return fileRedisRepository.saveAll(fileRedisList);
    }

    /**
     * Push in redis one object
     *
     * @param objRedis must not be {@literal null}.
     */
    @Override
    public void push(FileRedis objRedis) {
        fileRedisRepository.save(objRedis);
    }

    /**
     * Push a file to Redis that is fragmented.
     * @param dataUploadFileFragment Fragmented file data.
     * @return Created data.
     */
    @Override
    public FileRedis uploadFragment(ContainerDataUploadFileFragment dataUploadFileFragment) {
        return new FileRedis();
    }

    /**
     * Push a file to Redis that is fragmented.
     * @param dataUploadFileFragment Fragmented file data.
     * @return Created data.
     */
    @Override
    public Iterable<FileRedis> uploadFragment(
            @NotNull List<ContainerDataUploadFileFragment> dataUploadFileFragment) {
        final String loginUser = accessClaims.get("login", String.class);
        final String roleUser = accessClaims.get("role", String.class);
        List<FileRedis> fileRedis = new ArrayList<>();

        dataUploadFileFragment.forEach(uploadFile -> fileRedis.add(new FileRedis(
                loginUser,
                roleUser,
                uploadFile.originalName(),
                uploadFile.systemName(),
                uploadFile.systemPath().toString(),
                new ArrayList<>(),
                true,
                uploadFile.partsFragments()
                        .stream()
                        .map(Path::toString)
                        .toList()
        )));

        return fileRedisRepository.saveAll(fileRedis);
    }

}
