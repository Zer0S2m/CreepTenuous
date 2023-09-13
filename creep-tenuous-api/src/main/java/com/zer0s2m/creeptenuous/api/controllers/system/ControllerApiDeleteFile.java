package com.zer0s2m.creeptenuous.api.controllers.system;

import com.zer0s2m.creeptenuous.api.documentation.controllers.ControllerApiDeleteFileDoc;
import com.zer0s2m.creeptenuous.common.annotations.V1APIRestController;
import com.zer0s2m.creeptenuous.common.data.DataDeleteFileApi;
import com.zer0s2m.creeptenuous.common.enums.OperationRights;
import com.zer0s2m.creeptenuous.common.exceptions.FileObjectIsFrozenException;
import com.zer0s2m.creeptenuous.core.atomic.handlers.AtomicSystemCallManager;
import com.zer0s2m.creeptenuous.redis.events.FileRedisEventPublisher;
import com.zer0s2m.creeptenuous.redis.services.security.ServiceManagerRights;
import com.zer0s2m.creeptenuous.redis.services.system.ServiceDeleteFileRedis;
import com.zer0s2m.creeptenuous.services.system.impl.ServiceDeleteFileImpl;
import jakarta.validation.Valid;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.nio.file.Path;

@V1APIRestController
public class ControllerApiDeleteFile implements ControllerApiDeleteFileDoc {

    static final OperationRights operationRightsDirectory = OperationRights.SHOW;

    static final OperationRights operationRightsFile = OperationRights.DELETE;

    private final ServiceDeleteFileImpl serviceDeleteFile;

    private final ServiceDeleteFileRedis serviceDeleteFileRedis;

    private final ServiceManagerRights serviceManagerRights;

    private final FileRedisEventPublisher fileRedisEventPublisher;

    @Autowired
    public ControllerApiDeleteFile(ServiceDeleteFileImpl serviceDeleteFile,
                                   ServiceDeleteFileRedis serviceDeleteFileRedis,
                                   ServiceManagerRights serviceManagerRights,
                                   FileRedisEventPublisher fileRedisEventPublisher) {
        this.serviceDeleteFile = serviceDeleteFile;
        this.serviceDeleteFileRedis = serviceDeleteFileRedis;
        this.serviceManagerRights = serviceManagerRights;
        this.fileRedisEventPublisher = fileRedisEventPublisher;
    }

    /**
     * Delete file
     *
     * @param file        file delete data
     * @param accessToken raw JWT access token
     * @throws InvocationTargetException   Exception thrown by an invoked method or constructor.
     * @throws NoSuchMethodException       Thrown when a particular method cannot be found.
     * @throws InstantiationException      Thrown when an application tries to create an instance of a class
     *                                     using the newInstance method in class {@code Class}.
     * @throws IllegalAccessException      An IllegalAccessException is thrown when an application
     *                                     tries to reflectively create an instance
     * @throws FileObjectIsFrozenException file object is frozen
     */
    @Override
    @DeleteMapping("/file/delete")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void deleteFile(final @Valid @RequestBody @NotNull DataDeleteFileApi file,
                           @RequestHeader(name = "Authorization") String accessToken)
            throws InvocationTargetException, NoSuchMethodException,
            InstantiationException, IllegalAccessException, FileObjectIsFrozenException {
        serviceManagerRights.setAccessClaims(accessToken);
        serviceManagerRights.setIsWillBeCreated(false);

        serviceDeleteFileRedis.setAccessToken(accessToken);
        serviceDeleteFileRedis.setIsException(false);
        boolean isRightsDirectory = serviceDeleteFileRedis.checkRights(file.systemParents());

        if (!isRightsDirectory) {
            serviceManagerRights.checkRightsByOperation(operationRightsDirectory, file.systemParents());

            boolean isFrozen = serviceDeleteFileRedis.isFrozenFileSystemObject(file.systemParents());
            if (isFrozen) {
                throw new FileObjectIsFrozenException();
            }
        }

        boolean isRightsFile = serviceDeleteFileRedis.checkRights(List.of(file.systemFileName()));
        if (!isRightsFile) {
            serviceManagerRights.checkRightsByOperation(operationRightsFile, file.systemFileName());

            boolean isFrozen = serviceDeleteFileRedis.isFrozenFileSystemObject(file.systemFileName());
            if (isFrozen) {
                throw new FileObjectIsFrozenException();
            }
        }

        Path source = AtomicSystemCallManager.call(
                serviceDeleteFile,
                file.systemFileName(),
                file.systemParents()
        );
        serviceDeleteFileRedis.delete(source, file.systemFileName());
        fileRedisEventPublisher.publishDelete(file.systemFileName());
    }

}
