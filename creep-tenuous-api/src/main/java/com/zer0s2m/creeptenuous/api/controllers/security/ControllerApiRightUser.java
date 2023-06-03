package com.zer0s2m.creeptenuous.api.controllers.security;

import com.zer0s2m.creeptenuous.api.documentation.controllers.ControllerApiRightUserDoc;
import com.zer0s2m.creeptenuous.common.annotations.V1APIRestController;
import com.zer0s2m.creeptenuous.common.data.DataCreateRightUserApi;
import com.zer0s2m.creeptenuous.common.data.DataDeleteRightUserApi;
import com.zer0s2m.creeptenuous.common.enums.OperationRights;
import com.zer0s2m.creeptenuous.common.http.ResponseCreateRightUserApi;
import com.zer0s2m.creeptenuous.redis.services.security.ServiceManagerRights;
import com.zer0s2m.creeptenuous.redis.services.system.base.BaseServiceFileSystemRedis;
import jakarta.validation.Valid;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@V1APIRestController
public class ControllerApiRightUser implements ControllerApiRightUserDoc {

    private final ServiceManagerRights serviceManagerRights;

    private final BaseServiceFileSystemRedis serviceFileSystemRedis;

    @Autowired
    public ControllerApiRightUser(
            ServiceManagerRights serviceManagerRights,
            BaseServiceFileSystemRedis baseServiceFileSystemRedis) {
        this.serviceManagerRights = serviceManagerRights;
        this.serviceFileSystemRedis = baseServiceFileSystemRedis;
    }

    /**
     * Add rights for a user on a file system target
     * @param data Data to add
     * @param accessToken raw JWT access token
     * @return created data
     */
    @Override
    @PostMapping("/user/right")
    @ResponseStatus(code = HttpStatus.CREATED)
    public ResponseCreateRightUserApi add(
            final @Valid @RequestBody @NotNull DataCreateRightUserApi data,
            @RequestHeader(name = "Authorization") String accessToken
    ) {
        serviceFileSystemRedis.setAccessToken(accessToken);
        serviceManagerRights.setAccessToken(accessToken);

        return new ResponseCreateRightUserApi(
                data.systemName(), data.loginUser(), OperationRights.valueOf(data.right()));
    }

    /**
     * Delete rights for a user on a file system target
     * @param data data to delete
     * @param accessToken raw JWT access token
     */
    @Override
    @DeleteMapping("/user/right")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void delete(
            final @Valid @RequestBody DataDeleteRightUserApi data,
            @RequestHeader(name = "Authorization") String accessToken
    ) {
        serviceFileSystemRedis.setAccessToken(accessToken);
        serviceManagerRights.setAccessToken(accessToken);
    }
}
