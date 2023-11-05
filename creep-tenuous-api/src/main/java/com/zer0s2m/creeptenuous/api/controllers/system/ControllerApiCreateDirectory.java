package com.zer0s2m.creeptenuous.api.controllers.system;

import com.zer0s2m.creeptenuous.api.documentation.controllers.ControllerApiCreateDirectoryDoc;
import com.zer0s2m.creeptenuous.common.annotations.V1APIRestController;
import com.zer0s2m.creeptenuous.common.containers.ContainerDataCreateDirectory;
import com.zer0s2m.creeptenuous.common.data.DataCreateDirectoryApi;
import com.zer0s2m.creeptenuous.common.enums.OperationRights;
import com.zer0s2m.creeptenuous.common.exceptions.ExistsFileSystemObjectRedisException;
import com.zer0s2m.creeptenuous.common.exceptions.FileAlreadyExistsException;
import com.zer0s2m.creeptenuous.common.exceptions.FileObjectIsFrozenException;
import com.zer0s2m.creeptenuous.common.exceptions.messages.ExceptionDirectoryExistsMsg;
import com.zer0s2m.creeptenuous.common.http.ResponseCreateDirectoryApi;
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
import com.zer0s2m.creeptenuous.redis.services.system.ServiceCreateDirectoryRedis;
import com.zer0s2m.creeptenuous.security.jwt.providers.JwtProvider;
import com.zer0s2m.creeptenuous.security.jwt.utils.JwtUtils;
import com.zer0s2m.creeptenuous.services.system.ServiceCreateDirectory;
import com.zer0s2m.creeptenuous.services.system.core.ServiceBuildDirectoryPath;
import com.zer0s2m.creeptenuous.services.system.impl.ServiceCreateDirectoryImpl;
import jakarta.validation.Valid;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

@V1APIRestController
public class ControllerApiCreateDirectory implements ControllerApiCreateDirectoryDoc {

    static final OperationRights operationRights = OperationRights.CREATE;

    private final ServiceCreateDirectory createDirectory = new ServiceCreateDirectoryImpl();

    private final ServiceBuildDirectoryPath serviceBuildDirectoryPath = new ServiceBuildDirectoryPath();

    private final ServiceCreateDirectoryRedis serviceDirectoryRedis;

    private final ServiceManagerRights serviceManagerRights;

    private final ServiceCheckUniqueNameFileSystemObject serviceCheckUniqueNameFileSystemObject;

    private final JwtProvider jwtProvider;

    private final AtomicFileSystemControllerApiCreateDirectory atomicFileSystemControllerApiCreateDirectory =
            new AtomicFileSystemControllerApiCreateDirectory();

    @Autowired
    public ControllerApiCreateDirectory(
            ServiceCreateDirectoryRedis serviceDirectoryRedis,
            ServiceManagerRights serviceManagerRights,
            ServiceCheckUniqueNameFileSystemObject serviceCheckUniqueNameFileSystemObject,
            JwtProvider jwtProvider) {
        this.serviceDirectoryRedis = serviceDirectoryRedis;
        this.serviceManagerRights = serviceManagerRights;
        this.serviceCheckUniqueNameFileSystemObject = serviceCheckUniqueNameFileSystemObject;
        this.jwtProvider = jwtProvider;
    }

    /**
     * Creating a directory. Supports atomic file system mode.
     * <p>Called via {@link AtomicSystemCallManager} - {@link AtomicFileSystemControllerApiCreateDirectory#create(DataCreateDirectoryApi, String)}</p>
     *
     * @param directoryForm Directory create data.
     * @param accessToken   Raw JWT access token.
     * @return Result create directory.
     * @throws FileAlreadyExistsException           file already exists.
     * @throws InvocationTargetException            Exception thrown by an invoked method or constructor.
     * @throws NoSuchMethodException                Thrown when a particular method cannot be found.
     * @throws InstantiationException               Thrown when an application tries to create an instance of a class
     *                                              using the newInstance method in class {@code Class}.
     * @throws IllegalAccessException               An IllegalAccessException is thrown when an application
     *                                              tries to reflectively create an instance.
     * @throws IOException                          Signals that an I/O exception to some sort has occurred.
     * @throws ExistsFileSystemObjectRedisException Uniqueness of the name in the system under different directory
     *                                              levels.
     * @throws FileObjectIsFrozenException          File object is frozen.
     */
    @Override
    @PostMapping("/directory/create")
    @ResponseStatus(code = HttpStatus.CREATED)
    public final @NotNull ResponseCreateDirectoryApi createDirectory(
            final @Valid @RequestBody @NotNull DataCreateDirectoryApi directoryForm,
            @RequestHeader(name = "Authorization") String accessToken) throws FileAlreadyExistsException,
            InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException,
            IOException, ExistsFileSystemObjectRedisException, FileObjectIsFrozenException {
        return AtomicSystemCallManager.call(
                atomicFileSystemControllerApiCreateDirectory,
                directoryForm,
                accessToken
        );
    }

    @ExceptionHandler({FileAlreadyExistsException.class, java.nio.file.FileAlreadyExistsException.class})
    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
    public ExceptionDirectoryExistsMsg handleExceptionDirectoryExists(@NotNull FileAlreadyExistsException error) {
        return new ExceptionDirectoryExistsMsg(error.getMessage());
    }

    @CoreServiceFileSystem(method = "create")
    public final class AtomicFileSystemControllerApiCreateDirectory implements AtomicServiceFileSystem {

        /**
         * Creating a directory.
         *
         * @param directoryForm Directory create data.
         * @param accessToken   Raw JWT access token.
         * @return Result create directory.
         * @throws FileObjectIsFrozenException          File object is frozen.
         * @throws IOException                          Signals that an I/O exception to some sort has occurred.
         * @throws ExistsFileSystemObjectRedisException Uniqueness of the name in the system under different directory
         *                                              levels.
         */
        @Contract("_, _ -> new")
        @SuppressWarnings("unused")
        @AtomicFileSystem(
                name = "create-directory",
                handlers = {
                        @AtomicFileSystemExceptionHandler(
                                isExceptionMulti = true,
                                handler = ServiceFileSystemExceptionHandlerOperationCreate.class,
                                operation = ContextAtomicFileSystem.Operations.CREATE
                        )
                }
        )
        public @NotNull ResponseCreateDirectoryApi create(
                final @NotNull DataCreateDirectoryApi directoryForm, String accessToken)
                throws FileObjectIsFrozenException, IOException, ExistsFileSystemObjectRedisException {
            serviceDirectoryRedis.setAccessToken(accessToken);
            serviceDirectoryRedis.setIsException(false);
            boolean isRights = serviceDirectoryRedis.checkRights(
                    directoryForm.parents(),
                    directoryForm.systemParents(),
                    directoryForm.directoryName()
            );
            if (!isRights) {
                serviceManagerRights.setAccessClaims(accessToken);
                serviceManagerRights.setIsWillBeCreated(false);
                serviceManagerRights.checkRightsByOperation(operationRights, directoryForm.systemParents());

                boolean isFrozen = serviceDirectoryRedis.isFrozenFileSystemObject(directoryForm.systemParents());
                if (isFrozen) {
                    throw new FileObjectIsFrozenException();
                }
            }

            String loginUser = jwtProvider.getAccessClaims(JwtUtils.getPureAccessToken(accessToken))
                    .get("login", String.class);

            serviceCheckUniqueNameFileSystemObject.checkUniqueName(directoryForm.directoryName(),
                    WalkDirectoryInfo.getNamesFileSystemObject(
                            serviceBuildDirectoryPath.build(directoryForm.systemParents())), loginUser);

            ContainerDataCreateDirectory dataCreatedDirectory = createDirectory.create(
                    directoryForm.systemParents(), directoryForm.directoryName());

            serviceDirectoryRedis.create(dataCreatedDirectory);

            return new ResponseCreateDirectoryApi(
                    dataCreatedDirectory.realNameDirectory(),
                    dataCreatedDirectory.systemNameDirectory()
            );
        }

    }

}
