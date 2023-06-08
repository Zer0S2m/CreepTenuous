package com.zer0s2m.creeptenuous.api.controllers.system;

import com.zer0s2m.creeptenuous.api.documentation.controllers.ControllerApiUploadDirectoryDoc;
import com.zer0s2m.creeptenuous.common.annotations.V1APIRestController;
import com.zer0s2m.creeptenuous.common.enums.OperationRights;
import com.zer0s2m.creeptenuous.common.http.ResponseUploadDirectoryApi;
import com.zer0s2m.creeptenuous.core.handlers.AtomicSystemCallManager;
import com.zer0s2m.creeptenuous.redis.services.security.ServiceManagerRights;
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

@V1APIRestController
public class ControllerApiUploadDirectory implements ControllerApiUploadDirectoryDoc {

    static final OperationRights operationRights = OperationRights.UPLOAD;

    private final ServiceUploadDirectoryImpl serviceUploadDirectory;

    private final ServiceUploadDirectoryRedisImpl serviceUploadDirectoryRedis;

    private final ServiceManagerRights serviceManagerRights;

    @Autowired
    public ControllerApiUploadDirectory(
            ServiceUploadDirectoryImpl serviceUploadDirectory,
            ServiceUploadDirectoryRedisImpl serviceUploadDirectoryRedis,
            ServiceManagerRights serviceManagerRights
    ) {
        this.serviceUploadDirectory = serviceUploadDirectory;
        this.serviceUploadDirectoryRedis = serviceUploadDirectoryRedis;
        this.serviceManagerRights = serviceManagerRights;
    }

    /**
     * Upload directory (zip archive)
     * <p>Called method via {@link AtomicSystemCallManager} - {@link ServiceUploadDirectoryImpl#upload(Path, Path)}</p>
     * @param parents real names directories
     * @param systemParents parts of the system path - source
     * @param zipFile raw zip archive
     * @param accessToken raw JWT access token
     * @return result upload directory (zip archive)
     * @throws InvocationTargetException Exception thrown by an invoked method or constructor.
     * @throws NoSuchMethodException Thrown when a particular method cannot be found.
     * @throws InstantiationException Thrown when an application tries to create an instance of a class
     * using the newInstance method in class {@code Class}.
     * @throws IllegalAccessException An IllegalAccessException is thrown when an application
     * tries to reflectively create an instance
     * @throws IOException if an I/O error occurs or the parent directory does not exist
     */
    @Override
    @PostMapping(path = "/directory/upload")
    @ResponseStatus(code = HttpStatus.CREATED)
    public final ResponseUploadDirectoryApi upload(
            final @Nullable @RequestParam("parents") List<String> parents,
            final @Nullable @RequestParam("systemParents") List<String> systemParents,
            final @RequestPart("directory") MultipartFile zipFile,
            @RequestHeader(name = "Authorization") String accessToken
    ) throws InvocationTargetException, NoSuchMethodException, InstantiationException,
            IllegalAccessException, IOException {
        serviceUploadDirectoryRedis.setAccessToken(accessToken);
        boolean isRights = serviceUploadDirectoryRedis.checkRights(parents, systemParents, null, false);
        if (!isRights) {
            serviceManagerRights.setAccessClaims(accessToken);
            serviceManagerRights.setIsWillBeCreated(false);
            serviceManagerRights.checkRightsByOperation(operationRights, systemParents);
        }

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