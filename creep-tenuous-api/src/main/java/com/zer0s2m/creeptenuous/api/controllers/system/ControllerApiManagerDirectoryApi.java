package com.zer0s2m.creeptenuous.api.controllers.system;

import com.zer0s2m.creeptenuous.common.annotations.V1APIRestController;
import com.zer0s2m.creeptenuous.common.containers.ContainerDataBuilderDirectory;
import com.zer0s2m.creeptenuous.common.exceptions.NotValidLevelDirectoryException;
import com.zer0s2m.creeptenuous.common.exceptions.messages.ExceptionBadLevelDirectoryMsg;
import com.zer0s2m.creeptenuous.common.http.ResponseManagerDirectoryApi;
import com.zer0s2m.creeptenuous.services.redis.system.ServiceManagerDirectoryRedisImpl;
import com.zer0s2m.creeptenuous.services.system.impl.ServiceManagerDirectoryImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@V1APIRestController
public class ControllerApiManagerDirectoryApi {
    private final ServiceManagerDirectoryImpl builderDirectory;

    private final ServiceManagerDirectoryRedisImpl serviceManagerDirectoryRedis;

    @Autowired
    public ControllerApiManagerDirectoryApi(
            ServiceManagerDirectoryImpl builderDirectory,
            ServiceManagerDirectoryRedisImpl serviceManagerDirectoryRedis
    ) {
        this.builderDirectory = builderDirectory;
        this.serviceManagerDirectoryRedis = serviceManagerDirectoryRedis;
    }

    @GetMapping("/directory")
    @ResponseStatus(code = HttpStatus.OK)
    public ResponseManagerDirectoryApi manager(
            final @RequestParam(value = "level", defaultValue = "0") Integer level,
            final @RequestParam(value = "parents", defaultValue = "") List<String> parents,
            final @RequestParam(value = "systemParents", defaultValue = "") List<String> systemParents,
            @RequestHeader(name = "Authorization") String accessToken
    ) throws IOException, NotValidLevelDirectoryException, NoSuchFieldException {
        serviceManagerDirectoryRedis.setAccessToken(accessToken);
        serviceManagerDirectoryRedis.checkRights(parents, systemParents, null);

        ContainerDataBuilderDirectory rawData = builderDirectory.build(systemParents, level);
        List<Object> data = serviceManagerDirectoryRedis.build(rawData.namesSystemFileObject());

        return new ResponseManagerDirectoryApi(systemParents, level, data);
    }

    @ExceptionHandler(NotValidLevelDirectoryException.class)
    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
    public ExceptionBadLevelDirectoryMsg handleExceptionBadLevel(NotValidLevelDirectoryException error) {
        return new ExceptionBadLevelDirectoryMsg(error.getMessage());
    }
}