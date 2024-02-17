package com.zer0s2m.creeptenuous.api.controllers.user;

import com.zer0s2m.creeptenuous.api.documentation.controllers.ControllerApiControlFileSystemObjectDoc;
import com.zer0s2m.creeptenuous.api.annotation.V1APIRestController;
import com.zer0s2m.creeptenuous.common.data.DataControlFileSystemObjectApi;
import com.zer0s2m.creeptenuous.common.exceptions.NotFoundException;
import com.zer0s2m.creeptenuous.common.exceptions.NotFoundFileSystemObjectException;
import com.zer0s2m.creeptenuous.redis.services.system.base.BaseServiceFileSystemRedisManagerRightsAccess;
import com.zer0s2m.creeptenuous.redis.services.user.ServiceControlFileSystemObjectRedis;
import jakarta.validation.Valid;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@V1APIRestController
public class ControllerApiControlFileSystemObject implements ControllerApiControlFileSystemObjectDoc {

    private final ServiceControlFileSystemObjectRedis serviceControlFileSystemObjectRedis;

    private final BaseServiceFileSystemRedisManagerRightsAccess serviceFileSystemRedis;

    @Autowired
    public ControllerApiControlFileSystemObject(
            ServiceControlFileSystemObjectRedis serviceControlFileSystemObjectRedis,
            BaseServiceFileSystemRedisManagerRightsAccess baseServiceFileSystemRedis) {
        this.serviceControlFileSystemObjectRedis = serviceControlFileSystemObjectRedis;
        this.serviceFileSystemRedis = baseServiceFileSystemRedis;
    }

    /**
     * Freeze a file object by its name
     * @param data data to freeze the file object
     * @param accessToken access JWT token
     * @throws NotFoundException not found file system object
     */
    @Override
    @PostMapping("/file-system-object/freezing")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void freezingFileSystemObject(
            final @Valid @RequestBody @NotNull DataControlFileSystemObjectApi data,
            final @RequestHeader(name = "Authorization") String accessToken) throws NotFoundException {
        serviceFileSystemRedis.setAccessClaims(accessToken);
        serviceFileSystemRedis.setIsException(true);
        serviceFileSystemRedis.checkRights(data.fileSystemObject());

        if (!serviceFileSystemRedis.existsById(data.fileSystemObject())) {
            throw new NotFoundFileSystemObjectException();
        }

        serviceControlFileSystemObjectRedis.freezingFileSystemObject(data.fileSystemObject());
    }

    /**
     * Unfreeze a file object by its name
     * @param data data to unfreeze a file object
     * @param accessToken access JWT token
     * @throws NotFoundException not found file system object
     */
    @Override
    @DeleteMapping("file-system-object/unfreezing")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void unfreezingFileSystemObject(
            final @Valid @RequestBody @NotNull DataControlFileSystemObjectApi data,
            final @RequestHeader(name = "Authorization") String accessToken) throws NotFoundException {
        serviceFileSystemRedis.setAccessClaims(accessToken);
        serviceFileSystemRedis.setIsException(true);
        serviceFileSystemRedis.checkRights(data.fileSystemObject());

        if (!serviceFileSystemRedis.existsById(data.fileSystemObject())) {
            throw new NotFoundFileSystemObjectException();
        }

        serviceControlFileSystemObjectRedis.unfreezingFileSystemObject(data.fileSystemObject());
    }

}
