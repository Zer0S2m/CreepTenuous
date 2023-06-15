package com.zer0s2m.creeptenuous.api.controllers.system;

import com.zer0s2m.creeptenuous.api.documentation.controllers.ControllerApiRenameFileSystemObjectDoc;
import com.zer0s2m.creeptenuous.common.annotations.V1APIRestController;
import com.zer0s2m.creeptenuous.common.data.DataRenameFileSystemObjectApi;
import com.zer0s2m.creeptenuous.common.http.ResponseRenameFileSystemObjectDoc;
import com.zer0s2m.creeptenuous.redis.exceptions.NoExistsFileSystemObjectRedisException;
import com.zer0s2m.creeptenuous.redis.exceptions.NoRightsRedisException;
import com.zer0s2m.creeptenuous.redis.services.system.ServiceRenameFileSystemObjectRedis;
import jakarta.validation.Valid;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@V1APIRestController
public class ControllerApiRenameFileSystemObject implements ControllerApiRenameFileSystemObjectDoc {

    private final ServiceRenameFileSystemObjectRedis serviceRenameFileSystemObjectRedis;

    @Autowired
    public ControllerApiRenameFileSystemObject(
            ServiceRenameFileSystemObjectRedis serviceRenameFileSystemObjectRedis) {
        this.serviceRenameFileSystemObjectRedis = serviceRenameFileSystemObjectRedis;
    }

    /**
     * Rename file system object
     *
     * @param data        data to rename
     * @param accessToken raw JWT access token
     * @return new name
     * @throws NoExistsFileSystemObjectRedisException the file system object was not found in the database.
     * @throws NoRightsRedisException When the user has no execution right
     */
    @Override
    @PutMapping("/file-system-object/rename")
    @ResponseStatus(code = HttpStatus.OK)
    public ResponseRenameFileSystemObjectDoc rename(
            final @Valid @RequestBody @NotNull DataRenameFileSystemObjectApi data,
            @RequestHeader(name = "Authorization") String accessToken)
            throws NoExistsFileSystemObjectRedisException, NoRightsRedisException {
        serviceRenameFileSystemObjectRedis.setAccessClaims(accessToken);
        serviceRenameFileSystemObjectRedis.setIsException(true);

        serviceRenameFileSystemObjectRedis.checkRights(data.systemName());

        serviceRenameFileSystemObjectRedis.rename(data.systemName(), data.newRealName());

        return new ResponseRenameFileSystemObjectDoc(data.systemName(), data.newRealName());
    }

}
