package com.zer0s2m.creeptenuous.api.controllers.system;

import com.zer0s2m.creeptenuous.api.documentation.controllers.ControllerApiCreateDirectoryDoc;
import com.zer0s2m.creeptenuous.common.annotations.V1APIRestController;
import com.zer0s2m.creeptenuous.common.containers.ContainerDataCreateDirectory;
import com.zer0s2m.creeptenuous.common.data.DataCreateDirectoryApi;
import com.zer0s2m.creeptenuous.common.enums.OperationRights;
import com.zer0s2m.creeptenuous.common.exceptions.ExistsFileSystemObjectRedisException;
import com.zer0s2m.creeptenuous.common.exceptions.FileAlreadyExistsException;
import com.zer0s2m.creeptenuous.common.exceptions.messages.ExceptionDirectoryExistsMsg;
import com.zer0s2m.creeptenuous.common.http.ResponseCreateDirectoryApi;
import com.zer0s2m.creeptenuous.common.utils.WalkDirectoryInfo;
import com.zer0s2m.creeptenuous.core.handlers.AtomicSystemCallManager;
import com.zer0s2m.creeptenuous.redis.services.security.ServiceManagerRights;
import com.zer0s2m.creeptenuous.redis.services.system.ServiceCheckUniqueNameFileSystemObject;
import com.zer0s2m.creeptenuous.redis.services.system.ServiceCreateDirectoryRedis;
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
import java.util.List;

@V1APIRestController
public class ControllerApiCreateDirectory implements ControllerApiCreateDirectoryDoc {

    static final OperationRights operationRights = OperationRights.CREATE;

    private final ServiceCreateDirectoryImpl createDirectory;

    private final ServiceBuildDirectoryPath serviceBuildDirectoryPath;

    private final ServiceCreateDirectoryRedis serviceDirectoryRedis;

    private final ServiceManagerRights serviceManagerRights;

    private final ServiceCheckUniqueNameFileSystemObject serviceCheckUniqueNameFileSystemObject;

    @Autowired
    public ControllerApiCreateDirectory(ServiceCreateDirectoryImpl createDirectory,
                                        ServiceBuildDirectoryPath serviceBuildDirectoryPath,
                                        ServiceCreateDirectoryRedis serviceDirectoryRedis,
                                        ServiceManagerRights serviceManagerRights,
                                        ServiceCheckUniqueNameFileSystemObject serviceCheckUniqueNameFileSystemObject) {
        this.createDirectory = createDirectory;
        this.serviceBuildDirectoryPath = serviceBuildDirectoryPath;
        this.serviceDirectoryRedis = serviceDirectoryRedis;
        this.serviceManagerRights = serviceManagerRights;
        this.serviceCheckUniqueNameFileSystemObject = serviceCheckUniqueNameFileSystemObject;
    }

    /**
     * Create directory
     * <p>Called method via {@link AtomicSystemCallManager} - {@link ServiceCreateDirectoryImpl#create(List, String)}</p>
     *
     * @param directoryForm directory create data
     * @param accessToken   raw JWT access token
     * @return result create directory
     * @throws FileAlreadyExistsException           file already exists
     * @throws InvocationTargetException            Exception thrown by an invoked method or constructor.
     * @throws NoSuchMethodException                Thrown when a particular method cannot be found.
     * @throws InstantiationException               Thrown when an application tries to create an instance of a class
     *                                              using the newInstance method in class {@code Class}.
     * @throws IllegalAccessException               An IllegalAccessException is thrown when an application
     *                                              tries to reflectively create an instance
     * @throws IOException                          signals that an I/O exception of some sort has occurred
     * @throws ExistsFileSystemObjectRedisException uniqueness of the name in the system under different directory levels
     */
    @Contract("_, _ -> new")
    @Override
    @PostMapping("/directory/create")
    @ResponseStatus(code = HttpStatus.CREATED)
    public final @NotNull ResponseCreateDirectoryApi createDirectory(
            final @Valid @RequestBody @NotNull DataCreateDirectoryApi directoryForm,
            @RequestHeader(name = "Authorization") String accessToken) throws FileAlreadyExistsException,
            InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException,
            IOException, ExistsFileSystemObjectRedisException {
        serviceDirectoryRedis.setAccessToken(accessToken);
        boolean isRights = serviceDirectoryRedis.checkRights(
                directoryForm.parents(),
                directoryForm.systemParents(),
                directoryForm.directoryName(),
                false
        );
        if (!isRights) {
            serviceManagerRights.setAccessClaims(accessToken);
            serviceManagerRights.setIsWillBeCreated(false);
            serviceManagerRights.checkRightsByOperation(operationRights, directoryForm.systemParents());
        }

        serviceCheckUniqueNameFileSystemObject.checkUniqueName(directoryForm.directoryName(),
                WalkDirectoryInfo.getNamesFileSystemObject(
                        serviceBuildDirectoryPath.build(directoryForm.systemParents())));

        ContainerDataCreateDirectory dataCreatedDirectory = AtomicSystemCallManager.call(
                this.createDirectory,
                directoryForm.systemParents(),
                directoryForm.directoryName()
        );

        serviceDirectoryRedis.create(dataCreatedDirectory);

        return new ResponseCreateDirectoryApi(
                dataCreatedDirectory.realNameDirectory(),
                dataCreatedDirectory.systemNameDirectory()
        );
    }

    @ExceptionHandler({FileAlreadyExistsException.class, java.nio.file.FileAlreadyExistsException.class})
    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
    public ExceptionDirectoryExistsMsg handleExceptionDirectoryExists(@NotNull FileAlreadyExistsException error) {
        return new ExceptionDirectoryExistsMsg(error.getMessage());
    }

}
