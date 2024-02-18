package com.zer0s2m.creeptenuous.api.controllers.system;

import com.zer0s2m.creeptenuous.api.documentation.controllers.ControllerApiDeleteFileDoc;
import com.zer0s2m.creeptenuous.api.annotation.V1APIRestController;
import com.zer0s2m.creeptenuous.common.data.DataDeleteFileApi;
import com.zer0s2m.creeptenuous.common.enums.OperationRights;
import com.zer0s2m.creeptenuous.common.exceptions.FileObjectIsFrozenException;
import com.zer0s2m.creeptenuous.common.exceptions.NoSuchFileExistsException;
import com.zer0s2m.creeptenuous.core.atomic.AtomicFileSystem;
import com.zer0s2m.creeptenuous.core.atomic.AtomicFileSystemExceptionHandler;
import com.zer0s2m.creeptenuous.core.atomic.CoreServiceFileSystem;
import com.zer0s2m.creeptenuous.core.atomic.ContextAtomicFileSystem;
import com.zer0s2m.creeptenuous.core.atomic.AtomicSystemCallManager;
import com.zer0s2m.creeptenuous.core.atomic.ServiceFileSystemExceptionHandlerOperationDelete;
import com.zer0s2m.creeptenuous.core.atomic.AtomicServiceFileSystem;
import com.zer0s2m.creeptenuous.redis.events.FileRedisEventPublisher;
import com.zer0s2m.creeptenuous.redis.services.security.ServiceManagerRights;
import com.zer0s2m.creeptenuous.redis.services.system.ServiceDeleteFileRedis;
import com.zer0s2m.creeptenuous.core.services.ServiceDeleteFile;
import com.zer0s2m.creeptenuous.core.services.impl.ServiceDeleteFileImpl;
import jakarta.validation.Valid;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.nio.file.Path;

@V1APIRestController
public class ControllerApiDeleteFile implements ControllerApiDeleteFileDoc {

    static final OperationRights operationRightsDirectory = OperationRights.SHOW;

    static final OperationRights operationRightsFile = OperationRights.DELETE;

    private final ServiceDeleteFile serviceDeleteFile = new ServiceDeleteFileImpl();

    private final ServiceDeleteFileRedis serviceDeleteFileRedis;

    private final ServiceManagerRights serviceManagerRights;

    private final FileRedisEventPublisher fileRedisEventPublisher;

    private final AtomicFileSystemControllerApiDeleteFile atomicFileSystemControllerApiDeleteFile =
            new AtomicFileSystemControllerApiDeleteFile();

    @Autowired
    public ControllerApiDeleteFile(
            ServiceDeleteFileRedis serviceDeleteFileRedis,
            ServiceManagerRights serviceManagerRights,
            FileRedisEventPublisher fileRedisEventPublisher) {
        this.serviceDeleteFileRedis = serviceDeleteFileRedis;
        this.serviceManagerRights = serviceManagerRights;
        this.fileRedisEventPublisher = fileRedisEventPublisher;
    }

    /**
     * Deleting a file. Supports atomic file system mode.
     * <p>Called via {@link AtomicSystemCallManager} - {@link AtomicFileSystemControllerApiDeleteFile#delete(DataDeleteFileApi, String)}
     *
     * @param file        File delete data.
     * @param accessToken Raw JWT access token.
     * @throws InvocationTargetException   Exception thrown by an invoked method or constructor.
     * @throws NoSuchMethodException       Thrown when a particular method cannot be found.
     * @throws InstantiationException      Thrown when an application tries to create an instance of a class
     *                                     using the newInstance method in class {@code Class}.
     * @throws IllegalAccessException      An IllegalAccessException is thrown when an application
     *                                     tries to reflectively create an instance.
     * @throws FileObjectIsFrozenException File object is frozen.
     * @throws IOException                 Signals that an I/O exception to some sort has occurred.
     * @throws NoSuchFileExistsException   Exception raised when there is no file object.
     */
    @Override
    @DeleteMapping("/file/delete")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void deleteFile(final @Valid @RequestBody @NotNull DataDeleteFileApi file,
                           @RequestHeader(name = "Authorization") String accessToken)
            throws InvocationTargetException, NoSuchMethodException, InstantiationException,
            IllegalAccessException, FileObjectIsFrozenException, IOException, NoSuchFileExistsException {
        AtomicSystemCallManager.call(
                atomicFileSystemControllerApiDeleteFile,
                file,
                accessToken
        );
    }

    @CoreServiceFileSystem(method = "delete")
    public final class AtomicFileSystemControllerApiDeleteFile implements AtomicServiceFileSystem {

        /**
         * Deleting a file.
         *
         * @param file        File delete data.
         * @param accessToken Raw JWT access token.
         * @throws FileObjectIsFrozenException File object is frozen.
         * @throws NoSuchFileExistsException   Exception raised when there is no file object.
         * @throws IOException                 Signals that an I/O exception to some sort has occurred.
         */
        @SuppressWarnings("unused")
        @AtomicFileSystem(
                name = "delete-file",
                handlers = {
                        @AtomicFileSystemExceptionHandler(
                                isExceptionMulti = true,
                                handler = ServiceFileSystemExceptionHandlerOperationDelete.class,
                                operation = ContextAtomicFileSystem.Operations.DELETE
                        )
                }
        )
        public void delete(final @NotNull DataDeleteFileApi file, String accessToken)
                throws FileObjectIsFrozenException, NoSuchFileExistsException, IOException {
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

            Path source = serviceDeleteFile.delete(file.systemFileName(), file.systemParents());
            serviceDeleteFileRedis.delete(source, file.systemFileName());
            fileRedisEventPublisher.publishDelete(file.systemFileName());
        }

    }

}
