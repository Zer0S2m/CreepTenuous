package com.zer0s2m.creeptenuous.api.controllers.system;

import com.zer0s2m.creeptenuous.api.documentation.controllers.ControllerApiCreateFileDoc;
import com.zer0s2m.creeptenuous.common.annotations.V1APIRestController;
import com.zer0s2m.creeptenuous.common.containers.ContainerDataCreateFile;
import com.zer0s2m.creeptenuous.common.data.DataCreateFileApi;
import com.zer0s2m.creeptenuous.common.enums.OperationRights;
import com.zer0s2m.creeptenuous.common.enums.TypeFile;
import com.zer0s2m.creeptenuous.common.exceptions.ExistsFileSystemObjectRedisException;
import com.zer0s2m.creeptenuous.common.exceptions.FileObjectIsFrozenException;
import com.zer0s2m.creeptenuous.common.exceptions.NotFoundTypeFileException;
import com.zer0s2m.creeptenuous.common.exceptions.messages.FileAlreadyExistsMsg;
import com.zer0s2m.creeptenuous.common.exceptions.messages.NotFoundTypeFileMsg;
import com.zer0s2m.creeptenuous.common.http.ResponseCreateFileApi;
import com.zer0s2m.creeptenuous.common.utils.WalkDirectoryInfo;
import com.zer0s2m.creeptenuous.core.atomic.annotations.AtomicFileSystem;
import com.zer0s2m.creeptenuous.core.atomic.annotations.AtomicFileSystemExceptionHandler;
import com.zer0s2m.creeptenuous.core.atomic.annotations.CoreServiceFileSystem;
import com.zer0s2m.creeptenuous.core.atomic.context.ContextAtomicFileSystem;
import com.zer0s2m.creeptenuous.core.atomic.handlers.AtomicSystemCallManager;
import com.zer0s2m.creeptenuous.core.atomic.handlers.impl.ServiceFileSystemExceptionHandlerOperationCreate;
import com.zer0s2m.creeptenuous.core.atomic.services.AtomicServiceFileSystem;
import com.zer0s2m.creeptenuous.redis.services.security.ServiceManagerRights;
import com.zer0s2m.creeptenuous.redis.services.system.ServiceCheckUniqueNameFileSystemObject;
import com.zer0s2m.creeptenuous.redis.services.system.ServiceCreateFileRedis;
import com.zer0s2m.creeptenuous.security.jwt.providers.JwtProvider;
import com.zer0s2m.creeptenuous.security.jwt.utils.JwtUtils;
import com.zer0s2m.creeptenuous.services.system.ServiceCreateFile;
import com.zer0s2m.creeptenuous.services.system.core.ServiceBuildDirectoryPath;
import com.zer0s2m.creeptenuous.services.system.impl.ServiceCreateFileImpl;
import jakarta.validation.Valid;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.FileAlreadyExistsException;

@V1APIRestController
public class ControllerApiCreateFile implements ControllerApiCreateFileDoc {

    static final OperationRights operationRights = OperationRights.CREATE;

    private final ServiceCreateFile serviceCreateFile = new ServiceCreateFileImpl();

    private final ServiceBuildDirectoryPath serviceBuildDirectoryPath = new ServiceBuildDirectoryPath();

    private final ServiceCreateFileRedis serviceFileRedis;

    private final ServiceManagerRights serviceManagerRights;

    private final ServiceCheckUniqueNameFileSystemObject serviceCheckUniqueNameFileSystemObject;

    private final JwtProvider jwtProvider;

    private final AtomicFileSystemControllerApiCreateFile atomicFileSystemControllerApiCreateFile =
            new AtomicFileSystemControllerApiCreateFile();

    @Autowired
    public ControllerApiCreateFile(
            ServiceCreateFileRedis serviceFileRedis,
            ServiceManagerRights serviceManagerRights,
            ServiceCheckUniqueNameFileSystemObject serviceCheckUniqueNameFileSystemObject,
            JwtProvider jwtProvider) {
        this.serviceFileRedis = serviceFileRedis;
        this.serviceManagerRights = serviceManagerRights;
        this.serviceCheckUniqueNameFileSystemObject = serviceCheckUniqueNameFileSystemObject;
        this.jwtProvider = jwtProvider;
    }

    /**
     * Creating a file. Supports atomic file system mode.
     * <p>Called via {@link AtomicSystemCallManager} - {@link AtomicFileSystemControllerApiCreateFile#create(DataCreateFileApi, String)}.</p>
     *
     * @param file        File create data.
     * @param accessToken Raw JWT access token.
     * @return Result create file
     * @throws NoSuchMethodException                Thrown when a particular method cannot be found.
     * @throws InvocationTargetException            Exception thrown by an invoked method or constructor.
     * @throws InstantiationException               Thrown when an application tries to create an instance of a class
     *                                              using the newInstance method in class {@code Class}.
     * @throws IllegalAccessException               An IllegalAccessException is thrown when an application
     *                                              tries to reflectively create an instance.
     * @throws IOException                          Signals that an I/O exception to some sort has occurred.
     * @throws ExistsFileSystemObjectRedisException Uniqueness of the name in the system under different directory
     *                                              levels.
     * @throws FileObjectIsFrozenException          File object is frozen.
     * @throws NotFoundTypeFileException            File format not found to generate it.
     */
    @Override
    @PostMapping("/file/create")
    @ResponseStatus(code = HttpStatus.CREATED)
    public ResponseCreateFileApi createFile(final @Valid @RequestBody @NotNull DataCreateFileApi file,
                                            @RequestHeader(name = "Authorization") String accessToken)
            throws InvocationTargetException, NoSuchMethodException, IllegalAccessException,
            InstantiationException, IOException, ExistsFileSystemObjectRedisException, FileObjectIsFrozenException,
            NotFoundTypeFileException {
        return AtomicSystemCallManager.call(
                atomicFileSystemControllerApiCreateFile,
                file,
                accessToken
        );
    }

    @ExceptionHandler(NotFoundTypeFileException.class)
    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
    public NotFoundTypeFileMsg handleExceptionNotFoundTypeFile(@NotNull NotFoundTypeFileException error) {
        return new NotFoundTypeFileMsg(error.getMessage());
    }

    @ExceptionHandler(FileAlreadyExistsException.class)
    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
    public FileAlreadyExistsMsg handleExceptionFileExists(@NotNull FileAlreadyExistsException error) {
        return new FileAlreadyExistsMsg(error.getMessage());
    }

    @CoreServiceFileSystem(method = "create")
    public final class AtomicFileSystemControllerApiCreateFile implements AtomicServiceFileSystem {

        /**
         * Creating a file.
         *
         * @param file        File create data.
         * @param accessToken Raw JWT access token.
         * @return Result create file
         * @throws FileObjectIsFrozenException          File object is frozen.
         * @throws NotFoundTypeFileException            File format not found to generate it.
         * @throws IOException                          Signals that an I/O exception to some sort has occurred.
         * @throws ExistsFileSystemObjectRedisException Uniqueness of the name in the system under different directory
         *                                              levels.
         */
        @Contract("_, _ -> new")
        @AtomicFileSystem(
                name = "create-file",
                handlers = {
                        @AtomicFileSystemExceptionHandler(
                                isExceptionMulti = true,
                                handler = ServiceFileSystemExceptionHandlerOperationCreate.class,
                                operation = ContextAtomicFileSystem.Operations.CREATE
                        )
                }
        )
        public @NotNull ResponseCreateFileApi create(@NotNull DataCreateFileApi file, String accessToken)
                throws FileObjectIsFrozenException, NotFoundTypeFileException,
                IOException, ExistsFileSystemObjectRedisException {
            serviceFileRedis.setAccessToken(accessToken);
            serviceFileRedis.setIsException(false);
            boolean isRights = serviceFileRedis.checkRights(file.parents(), file.systemParents(), null);

            if (!isRights) {
                serviceManagerRights.setAccessClaims(accessToken);
                serviceManagerRights.setIsWillBeCreated(false);
                serviceManagerRights.checkRightsByOperation(operationRights, file.systemParents());

                boolean isFrozen = serviceFileRedis.isFrozenFileSystemObject(file.systemParents());
                if (isFrozen) {
                    throw new FileObjectIsFrozenException();
                }
            }

            String loginUser = jwtProvider.getAccessClaims(JwtUtils.getPureAccessToken(accessToken))
                    .get("login", String.class);

            serviceCheckUniqueNameFileSystemObject.checkUniqueName(
                    file.fileName() + "." + TypeFile.getExtension(file.typeFile()),
                    WalkDirectoryInfo.getNamesFileSystemObject(
                            serviceBuildDirectoryPath.build(file.systemParents())), loginUser);

            ContainerDataCreateFile dataCreatedFile = serviceCreateFile.create(
                    file.systemParents(), file.fileName(), file.typeFile());

            serviceFileRedis.create(dataCreatedFile);

            return new ResponseCreateFileApi(dataCreatedFile.realNameFile(), dataCreatedFile.systemNameFile());
        }

    }

}
