package com.zer0s2m.creeptenuous.api.controllers.system;

import com.zer0s2m.creeptenuous.api.documentation.controllers.ControllerApiMoveFileDoc;
import com.zer0s2m.creeptenuous.common.annotations.V1APIRestController;
import com.zer0s2m.creeptenuous.common.data.DataMoveFileApi;
import com.zer0s2m.creeptenuous.common.enums.OperationRights;
import com.zer0s2m.creeptenuous.common.exceptions.FileObjectIsFrozenException;
import com.zer0s2m.creeptenuous.common.utils.CloneList;
import com.zer0s2m.creeptenuous.core.atomic.handlers.AtomicSystemCallManager;
import com.zer0s2m.creeptenuous.redis.services.security.ServiceManagerRights;
import com.zer0s2m.creeptenuous.redis.services.system.ServiceMoveFileRedis;
import com.zer0s2m.creeptenuous.services.system.ServiceMoveFile;
import com.zer0s2m.creeptenuous.services.system.impl.ServiceMoveFileImpl;
import jakarta.validation.Valid;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.lang.reflect.InvocationTargetException;
import java.nio.file.Path;
import java.util.Objects;
import java.util.List;

@V1APIRestController
public class ControllerApiMoveFile implements ControllerApiMoveFileDoc {

    static final OperationRights operationRightsDirectory = OperationRights.SHOW;

    static final OperationRights operationRights = OperationRights.MOVE;

    private final ServiceMoveFile serviceMoveFile = new ServiceMoveFileImpl();

    private final ServiceMoveFileRedis serviceMoveFileRedis;

    private final ServiceManagerRights serviceManagerRights;

    @Autowired
    public ControllerApiMoveFile(
            ServiceMoveFileRedis serviceMoveFileRedis,
            ServiceManagerRights serviceManagerRights) {
        this.serviceMoveFileRedis = serviceMoveFileRedis;
        this.serviceManagerRights = serviceManagerRights;
    }

    /**
     * Move file
     * <p>Called method via {@link AtomicSystemCallManager} - {@link ServiceMoveFileImpl#move(String, List, List)}
     * or {@link ServiceMoveFileImpl#move(List, List, List)}</p>
     *
     * @param file        file move data
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
    @PutMapping("/file/move")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void move(final @Valid @RequestBody @NotNull DataMoveFileApi file,
                     @RequestHeader(name = "Authorization") String accessToken
    ) throws InvocationTargetException, NoSuchMethodException, InstantiationException,
            IllegalAccessException, FileObjectIsFrozenException {
        serviceManagerRights.setAccessClaims(accessToken);
        serviceManagerRights.setIsWillBeCreated(false);

        serviceMoveFileRedis.setAccessToken(accessToken);
        serviceMoveFileRedis.setIsException(false);

        List<String> mergeRealAndSystemParents = CloneList.cloneOneLevel(
                file.systemParents(),
                file.systemToParents()
        );
        boolean isRights = serviceMoveFileRedis.checkRights(mergeRealAndSystemParents);
        if (!isRights) {
            serviceManagerRights.checkRightsByOperation(operationRightsDirectory, mergeRealAndSystemParents);

            boolean isFrozen = serviceMoveFileRedis.isFrozenFileSystemObject(mergeRealAndSystemParents);
            if (isFrozen) {
                throw new FileObjectIsFrozenException();
            }
        }

        if (file.systemFileName() != null) {
            boolean isRightFile = serviceMoveFileRedis.checkRights(List.of(file.systemFileName()));
            if (!isRightFile) {
                serviceManagerRights.checkRightsByOperation(operationRights, file.systemFileName());

                boolean isFrozen = serviceMoveFileRedis.isFrozenFileSystemObject(file.systemFileName());
                if (isFrozen) {
                    throw new FileObjectIsFrozenException();
                }
            }

            Path newPathFile = AtomicSystemCallManager.call(
                    serviceMoveFile,
                    file.systemFileName(),
                    file.systemParents(),
                    file.systemToParents()
            );
            serviceMoveFileRedis.move(newPathFile, file.systemFileName());
        } else {
            boolean isRightFile = serviceMoveFileRedis.checkRights(file.systemNameFiles());
            if (!isRightFile) {
                serviceManagerRights.checkRightsByOperation(operationRights, file.systemNameFiles());

                boolean isFrozen = serviceMoveFileRedis.isFrozenFileSystemObject(file.systemNameFiles());
                if (isFrozen) {
                    throw new FileObjectIsFrozenException();
                }
            }

            Path newPathsFile = AtomicSystemCallManager.call(
                    serviceMoveFile,
                    Objects.requireNonNull(file.systemNameFiles()),
                    file.systemParents(),
                    file.systemToParents()
            );
            serviceMoveFileRedis.move(newPathsFile, file.systemNameFiles());
        }
    }

}
