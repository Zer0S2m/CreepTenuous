package com.zer0s2m.creeptenuous.api.controllers.system;

import com.zer0s2m.creeptenuous.api.documentation.controllers.ControllerApiMoveFileDoc;
import com.zer0s2m.creeptenuous.common.annotations.V1APIRestController;
import com.zer0s2m.creeptenuous.common.data.DataMoveFileApi;
import com.zer0s2m.creeptenuous.common.enums.OperationRights;
import com.zer0s2m.creeptenuous.common.exceptions.FileObjectIsFrozenException;
import com.zer0s2m.creeptenuous.common.utils.CloneList;
import com.zer0s2m.creeptenuous.core.atomic.annotations.AtomicFileSystem;
import com.zer0s2m.creeptenuous.core.atomic.annotations.AtomicFileSystemExceptionHandler;
import com.zer0s2m.creeptenuous.core.atomic.annotations.CoreServiceFileSystem;
import com.zer0s2m.creeptenuous.core.atomic.context.ContextAtomicFileSystem;
import com.zer0s2m.creeptenuous.core.atomic.handlers.AtomicSystemCallManager;
import com.zer0s2m.creeptenuous.core.atomic.handlers.impl.ServiceFileSystemExceptionHandlerOperationMove;
import com.zer0s2m.creeptenuous.core.atomic.services.AtomicServiceFileSystem;
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

import java.io.IOException;
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

    private final AtomicFileSystemControllerApiMoveFile atomicFileSystemControllerApiMoveFile =
            new AtomicFileSystemControllerApiMoveFile();

    @Autowired
    public ControllerApiMoveFile(
            ServiceMoveFileRedis serviceMoveFileRedis,
            ServiceManagerRights serviceManagerRights) {
        this.serviceMoveFileRedis = serviceMoveFileRedis;
        this.serviceManagerRights = serviceManagerRights;
    }

    /**
     * Moving files. Supports atomic file system mode.
     * <p>Called via {@link AtomicSystemCallManager} - {@link AtomicFileSystemControllerApiMoveFile#move(DataMoveFileApi, String)}</p>
     *
     * @param file        File move data.
     * @param accessToken Raw JWT access token
     * @throws InvocationTargetException   Exception thrown by an invoked method or constructor.
     * @throws NoSuchMethodException       Thrown when a particular method cannot be found.
     * @throws InstantiationException      Thrown when an application tries to create an instance of a class
     *                                     using the newInstance method in class {@code Class}.
     * @throws IllegalAccessException      An IllegalAccessException is thrown when an application
     *                                     tries to reflectively create an instance.
     * @throws FileObjectIsFrozenException File object is frozen.
     * @throws IOException                 Signals that an I/O exception to some sort has occurred.
     */
    @Override
    @PutMapping("/file/move")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void move(final @Valid @RequestBody @NotNull DataMoveFileApi file,
                     @RequestHeader(name = "Authorization") String accessToken
    ) throws InvocationTargetException, NoSuchMethodException, InstantiationException,
            IllegalAccessException, FileObjectIsFrozenException, IOException {
        AtomicSystemCallManager.call(
                atomicFileSystemControllerApiMoveFile,
                file,
                accessToken
        );
    }

    @CoreServiceFileSystem(method = "move")
    public final class AtomicFileSystemControllerApiMoveFile implements AtomicServiceFileSystem {

        /**
         * Moving files.
         *
         * @param file        File move data.
         * @param accessToken Raw JWT access token
         * @throws FileObjectIsFrozenException File object is frozen.
         * @throws IOException                 Signals that an I/O exception to some sort has occurred.
         */
        @SuppressWarnings("unused")
        @AtomicFileSystem(
                name = "move-file",
                handlers = {
                        @AtomicFileSystemExceptionHandler(
                                isExceptionMulti = true,
                                handler = ServiceFileSystemExceptionHandlerOperationMove.class,
                                operation = ContextAtomicFileSystem.Operations.MOVE
                        )
                }
        )
        public void move(final @NotNull DataMoveFileApi file, String accessToken) throws FileObjectIsFrozenException,
                IOException {
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

                Path newPathFile = serviceMoveFile.move(
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

                Path newPathsFile = serviceMoveFile.move(
                        Objects.requireNonNull(file.systemNameFiles()),
                        file.systemParents(),
                        file.systemToParents()
                );
                serviceMoveFileRedis.move(newPathsFile, file.systemNameFiles());
            }
        }

    }

}
