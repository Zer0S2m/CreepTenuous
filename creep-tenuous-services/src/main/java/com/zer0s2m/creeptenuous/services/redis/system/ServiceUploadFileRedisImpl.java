package com.zer0s2m.creeptenuous.services.redis.system;

import com.zer0s2m.creeptenuous.common.containers.ContainerDataUploadFile;
import com.zer0s2m.creeptenuous.redis.models.FileRedis;
import com.zer0s2m.creeptenuous.redis.repositories.FileRedisRepository;
import com.zer0s2m.creeptenuous.redis.repositories.DirectoryRedisRepository;
import com.zer0s2m.creeptenuous.redis.services.system.ServiceCreateFileRedis;
import com.zer0s2m.creeptenuous.redis.services.system.ServiceUploadFileRedis;
import com.zer0s2m.creeptenuous.security.jwt.providers.JwtProvider;
import com.zer0s2m.creeptenuous.services.redis.system.base.BaseServiceFileSystemRedisImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("service-upload-file-redis")
public class ServiceUploadFileRedisImpl extends BaseServiceFileSystemRedisImpl implements ServiceUploadFileRedis {
    @Autowired
    public ServiceUploadFileRedisImpl(
            DirectoryRedisRepository directoryRedisRepository,
            FileRedisRepository fileRedisRepository,
            JwtProvider jwtProvider
    ) {
        super(directoryRedisRepository, fileRedisRepository, jwtProvider);
    }

    /**
     * Push data in redis create file
     * @param dataCreatedFile data upload file
     */
    @Override
    public void create(ContainerDataUploadFile dataCreatedFile) {
        String loginUser = accessClaims.get("login", String.class);
        String roleUser = accessClaims.get("role", String.class);

        FileRedis objRedis = ServiceCreateFileRedis.getObjRedis(
                loginUser,
                roleUser,
                dataCreatedFile.realNameFile(),
                dataCreatedFile.systemNameFile(),
                dataCreatedFile.systemPathFile().toString()
        );

        push(objRedis);
    }

    @Override
    public void push(FileRedis objRedis) {
        fileRedisRepository.save(objRedis);
    }
}
