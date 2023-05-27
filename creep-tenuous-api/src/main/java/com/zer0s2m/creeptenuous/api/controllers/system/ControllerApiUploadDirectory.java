package com.zer0s2m.creeptenuous.api.controllers.system;

import com.zer0s2m.creeptenuous.common.annotations.V1APIRestController;
import com.zer0s2m.creeptenuous.common.http.ResponseUploadDirectoryApi;
import com.zer0s2m.creeptenuous.core.handlers.AtomicSystemCallManager;
import com.zer0s2m.creeptenuous.services.redis.system.ServiceUploadDirectoryRedisImpl;
import com.zer0s2m.creeptenuous.services.system.impl.ServiceUploadDirectoryImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.ExecutionException;

@V1APIRestController
public class ControllerApiUploadDirectory {
    private final ServiceUploadDirectoryImpl serviceUploadDirectory;

    private final ServiceUploadDirectoryRedisImpl serviceUploadDirectoryRedis;

    @Autowired
    public ControllerApiUploadDirectory(
            ServiceUploadDirectoryImpl serviceUploadDirectory,
            ServiceUploadDirectoryRedisImpl serviceUploadDirectoryRedis
    ) {
        this.serviceUploadDirectory = serviceUploadDirectory;
        this.serviceUploadDirectoryRedis = serviceUploadDirectoryRedis;
    }

    @PostMapping(path = "/directory/upload")
    @ResponseStatus(code = HttpStatus.CREATED)
    public final ResponseUploadDirectoryApi upload(
            final @Nullable @RequestParam("parents") List<String> parents,
            final @Nullable @RequestParam("systemParents") List<String> systemParents,
            final @RequestPart("directory") MultipartFile zipFile,
            @RequestHeader(name = "Authorization") String accessToken
    ) throws InvocationTargetException, NoSuchMethodException, InstantiationException,
            IllegalAccessException, ExecutionException, InterruptedException, IOException {
        serviceUploadDirectoryRedis.setAccessToken(accessToken);
        serviceUploadDirectoryRedis.checkRights(parents, systemParents, null);

        Path systemPath = serviceUploadDirectory.getPath(systemParents);

        final ResponseUploadDirectoryApi finalData = AtomicSystemCallManager.call(
                serviceUploadDirectory,
                systemPath,
                serviceUploadDirectory.getNewPathZipFile(systemPath, zipFile)
        );
        serviceUploadDirectoryRedis.upload(finalData.data());
        return finalData;
    }
}
