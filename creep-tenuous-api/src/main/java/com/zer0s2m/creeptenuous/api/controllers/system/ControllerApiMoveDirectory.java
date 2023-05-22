package com.zer0s2m.creeptenuous.api.controllers.system;

import com.zer0s2m.creeptenuous.common.annotations.V1APIRestController;
import com.zer0s2m.creeptenuous.common.containers.ContainerDataMoveDirectory;
import com.zer0s2m.creeptenuous.common.data.DataMoveDirectoryApi;
import com.zer0s2m.creeptenuous.common.utils.CloneList;
import com.zer0s2m.creeptenuous.core.handlers.AtomicSystemCallManager;
import com.zer0s2m.creeptenuous.services.redis.system.ServiceMoveDirectoryRedisImpl;
import com.zer0s2m.creeptenuous.services.system.impl.ServiceMoveDirectoryImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

@V1APIRestController
public class ControllerApiMoveDirectory {
    private final ServiceMoveDirectoryImpl serviceMoveDirectory;

    private final ServiceMoveDirectoryRedisImpl serviceMoveDirectoryRedis;

    @Autowired
    public ControllerApiMoveDirectory(
            ServiceMoveDirectoryImpl serviceMoveDirectory,
            ServiceMoveDirectoryRedisImpl serviceMoveDirectoryRedis
    ) {
        this.serviceMoveDirectory = serviceMoveDirectory;
        this.serviceMoveDirectoryRedis = serviceMoveDirectoryRedis;
    }

    @PostMapping("/directory/move")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public final void move(
            final @Valid @RequestBody DataMoveDirectoryApi dataDirectory,
            @RequestHeader(name = "Authorization") String accessToken
    ) throws InvocationTargetException, NoSuchMethodException,
            InstantiationException, IllegalAccessException {
        List<String> mergeParents = CloneList.cloneOneLevel(
                dataDirectory.systemParents(),
                dataDirectory.systemToParents()
        );
        serviceMoveDirectoryRedis.setAccessToken(accessToken);
        serviceMoveDirectoryRedis.setEnableCheckIsNameDirectory(true);
        serviceMoveDirectoryRedis.checkRights(
                dataDirectory.parents(),
                mergeParents,
                dataDirectory.systemNameDirectory()
        );
        ContainerDataMoveDirectory infoMoving = AtomicSystemCallManager.call(
                serviceMoveDirectory,
                dataDirectory.systemParents(),
                dataDirectory.systemToParents(),
                dataDirectory.systemNameDirectory(),
                dataDirectory.method()
        );
        serviceMoveDirectoryRedis.move(infoMoving);
    }
}
