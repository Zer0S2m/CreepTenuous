package com.zer0s2m.creeptenuous.api.controllers.system;

import com.zer0s2m.creeptenuous.api.documentation.controllers.ControllerApiManagerDirectoryApiDoc;
import com.zer0s2m.creeptenuous.common.annotations.V1APIRestController;
import com.zer0s2m.creeptenuous.common.containers.ContainerDataBuilderDirectory;
import com.zer0s2m.creeptenuous.common.data.DataManagerDirectoryApi;
import com.zer0s2m.creeptenuous.common.exceptions.NotValidLevelDirectoryException;
import com.zer0s2m.creeptenuous.common.exceptions.messages.ExceptionBadLevelDirectoryMsg;
import com.zer0s2m.creeptenuous.common.http.ResponseManagerDirectoryApi;
import com.zer0s2m.creeptenuous.services.redis.system.ServiceManagerDirectoryRedisImpl;
import com.zer0s2m.creeptenuous.services.system.impl.ServiceManagerDirectoryImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@V1APIRestController
public class ControllerApiManagerDirectoryApi implements ControllerApiManagerDirectoryApiDoc {
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

    /**
     * Manager directory - get all directories by level
     * @param data data manager directory
     * @return result manager build info in directory
     * @throws IOException if an I/O error occurs or the parent directory does not exist
     * @throws NotValidLevelDirectoryException  invalid level directory
     */
    @Override
    @PostMapping("/directory")
    @ResponseStatus(code = HttpStatus.OK)
    public ResponseManagerDirectoryApi manager(
            final @Valid @RequestBody DataManagerDirectoryApi data,
            @RequestHeader(name = "Authorization") String accessToken
    ) throws IOException, NotValidLevelDirectoryException {
        serviceManagerDirectoryRedis.setAccessToken(accessToken);
        serviceManagerDirectoryRedis.checkRights(
                data.parents(),
                data.systemParents(),
                null
        );

        ContainerDataBuilderDirectory rawData = builderDirectory.build(
                data.systemParents(),
                data.level()
        );
        List<Object> result = serviceManagerDirectoryRedis.build(rawData.namesSystemFileObject());

        return new ResponseManagerDirectoryApi(data.systemParents(), data.level(), result);
    }

    @ExceptionHandler(NotValidLevelDirectoryException.class)
    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
    public ExceptionBadLevelDirectoryMsg handleExceptionBadLevel(NotValidLevelDirectoryException error) {
        return new ExceptionBadLevelDirectoryMsg(error.getMessage());
    }
}