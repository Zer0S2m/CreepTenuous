package com.zer0s2m.creeptenuous.api.controllers.system;

import com.zer0s2m.creeptenuous.api.documentation.controllers.ControllerApiMoveDirectoryDoc;
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
public class ControllerApiMoveDirectory implements ControllerApiMoveDirectoryDoc {
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

    /**
     * Move directory
     * <p>Called method via {@link AtomicSystemCallManager} - {@link ServiceMoveDirectoryImpl#move(List, List, String, Integer)}</p>
     * @param dataDirectory directory move data
     * @param accessToken raw JWT access token
     * @throws InvocationTargetException Exception thrown by an invoked method or constructor.
     * @throws NoSuchMethodException Thrown when a particular method cannot be found.
     * @throws InstantiationException Thrown when an application tries to create an instance of a class
     * using the newInstance method in class {@code Class}.
     * @throws IllegalAccessException An IllegalAccessException is thrown when an application
     * tries to reflectively create an instance
     */
    @Override
    @PutMapping("/directory/move")
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
                dataDirectory.systemDirectoryName()
        );
        ContainerDataMoveDirectory infoMoving = AtomicSystemCallManager.call(
                serviceMoveDirectory,
                dataDirectory.systemParents(),
                dataDirectory.systemToParents(),
                dataDirectory.systemDirectoryName(),
                dataDirectory.method()
        );
        serviceMoveDirectoryRedis.move(infoMoving);
    }
}
