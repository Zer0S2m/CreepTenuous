package com.zer0s2m.creeptenuous.api.controllers.system;

import com.zer0s2m.creeptenuous.api.documentation.controllers.ControllerApiRenameFileSystemObjectDoc;
import com.zer0s2m.creeptenuous.common.annotations.V1APIRestController;
import com.zer0s2m.creeptenuous.common.data.DataRenameFileSystemObjectApi;
import com.zer0s2m.creeptenuous.common.enums.OperationRights;
import com.zer0s2m.creeptenuous.common.exceptions.FileObjectIsFrozenException;
import com.zer0s2m.creeptenuous.common.http.ResponseRenameFileSystemObjectDoc;
import com.zer0s2m.creeptenuous.common.exceptions.NoExistsFileSystemObjectRedisException;
import com.zer0s2m.creeptenuous.common.exceptions.NoRightsRedisException;
import com.zer0s2m.creeptenuous.redis.services.security.ServiceManagerRights;
import com.zer0s2m.creeptenuous.redis.services.system.ServiceRenameFileSystemObjectRedis;
import jakarta.validation.Valid;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@V1APIRestController
public class ControllerApiRenameFileSystemObject implements ControllerApiRenameFileSystemObjectDoc {

    public static final OperationRights operationRights = OperationRights.RENAME;

    private final ServiceRenameFileSystemObjectRedis serviceRenameFileSystemObjectRedis;

    private final ServiceManagerRights serviceManagerRights;

    @Autowired
    public ControllerApiRenameFileSystemObject(
            ServiceRenameFileSystemObjectRedis serviceRenameFileSystemObjectRedis,
            ServiceManagerRights serviceManagerRights) {
        this.serviceRenameFileSystemObjectRedis = serviceRenameFileSystemObjectRedis;
        this.serviceManagerRights = serviceManagerRights;
    }

    /**
     * Rename file system object
     *
     * @param data        data to rename
     * @param accessToken raw JWT access token
     * @return new name
     * @throws NoExistsFileSystemObjectRedisException the file system object was not found in the database.
     * @throws NoRightsRedisException                 When the user has no execution right
     * @throws FileObjectIsFrozenException            file object is frozen
     */
    @Override
    @PutMapping("/file-system-object/rename")
    @ResponseStatus(code = HttpStatus.OK)
    public ResponseRenameFileSystemObjectDoc rename(
            final @Valid @RequestBody @NotNull DataRenameFileSystemObjectApi data,
            @RequestHeader(name = "Authorization") String accessToken)
            throws NoExistsFileSystemObjectRedisException, NoRightsRedisException, FileObjectIsFrozenException {
        serviceRenameFileSystemObjectRedis.setAccessClaims(accessToken);
        serviceRenameFileSystemObjectRedis.setIsException(false);

        serviceManagerRights.setAccessToken(accessToken);
        serviceManagerRights.setIsDirectory(false);
        serviceManagerRights.setIsWillBeCreated(false);

        boolean isRight = serviceRenameFileSystemObjectRedis.checkRights(data.systemName());
        if (!isRight) {
            serviceManagerRights.checkRightsByOperation(operationRights, data.systemName());

            boolean isFrozen = serviceRenameFileSystemObjectRedis.isFrozenFileSystemObject(data.systemName());
            if (isFrozen) {
                throw new FileObjectIsFrozenException();
            }
        }

        serviceRenameFileSystemObjectRedis.rename(data.systemName(), data.newRealName());

        return new ResponseRenameFileSystemObjectDoc(data.systemName(), data.newRealName());
    }

}
