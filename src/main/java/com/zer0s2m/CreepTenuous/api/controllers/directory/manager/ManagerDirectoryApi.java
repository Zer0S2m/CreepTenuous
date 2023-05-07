package com.zer0s2m.CreepTenuous.api.controllers.directory.manager;

import com.zer0s2m.CreepTenuous.api.controllers.directory.manager.data.DataManagerDirectory;
import com.zer0s2m.CreepTenuous.api.core.annotations.V1APIRestController;
import com.zer0s2m.CreepTenuous.providers.redis.controllers.CheckRightsActionFileSystem;
import com.zer0s2m.CreepTenuous.services.directory.manager.containers.ContainerDataBuilder;
import com.zer0s2m.CreepTenuous.services.directory.manager.exceptions.NotValidLevelDirectoryException;
import com.zer0s2m.CreepTenuous.services.directory.manager.services.impl.ServiceManagerDirectory;
import com.zer0s2m.CreepTenuous.services.directory.manager.exceptions.messages.ExceptionBadLevelDirectoryMsg;
import com.zer0s2m.CreepTenuous.providers.build.os.services.CheckIsExistsDirectoryApi;
import com.zer0s2m.CreepTenuous.services.directory.manager.services.impl.ServiceManagerDirectoryRedis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@V1APIRestController
public class ManagerDirectoryApi implements CheckIsExistsDirectoryApi, CheckRightsActionFileSystem {
    private final ServiceManagerDirectory builderDirectory;

    private final ServiceManagerDirectoryRedis serviceManagerDirectoryRedis;

    @Autowired
    public ManagerDirectoryApi(
            ServiceManagerDirectory builderDirectory,
            ServiceManagerDirectoryRedis serviceManagerDirectoryRedis
    ) {
        this.builderDirectory = builderDirectory;
        this.serviceManagerDirectoryRedis = serviceManagerDirectoryRedis;
    }

    @GetMapping("/directory")
    @ResponseStatus(code = HttpStatus.OK)
    public DataManagerDirectory manager(
            final @RequestParam(value = "level", defaultValue = "0") Integer level,
            final @RequestParam(value = "parents", defaultValue = "") List<String> parents,
            final @RequestParam(value = "systemParents", defaultValue = "") List<String> systemParents,
            @RequestHeader(name = "Authorization") String accessToken
    ) throws IOException, NotValidLevelDirectoryException, NoSuchFieldException {
        serviceManagerDirectoryRedis.setAccessToken(accessToken);
        serviceManagerDirectoryRedis.checkRights(parents, systemParents, null);

        ContainerDataBuilder rawData = builderDirectory.build(systemParents, level);
        List<Object> data = serviceManagerDirectoryRedis.build(rawData.namesSystemFileObject());

        return new DataManagerDirectory(systemParents, level, data);
    }

    @ExceptionHandler(NotValidLevelDirectoryException.class)
    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
    public ExceptionBadLevelDirectoryMsg handleExceptionBadLevel(NotValidLevelDirectoryException error) {
        return new ExceptionBadLevelDirectoryMsg(error.getMessage());
    }
}