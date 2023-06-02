package com.zer0s2m.creeptenuous.api.controllers.system;

import com.zer0s2m.creeptenuous.api.documentation.controllers.ControllerApiCreateDirectoryDoc;
import com.zer0s2m.creeptenuous.common.annotations.V1APIRestController;
import com.zer0s2m.creeptenuous.common.containers.ContainerDataCreateDirectory;
import com.zer0s2m.creeptenuous.common.data.DataCreateDirectoryApi;
import com.zer0s2m.creeptenuous.common.exceptions.FileAlreadyExistsException;
import com.zer0s2m.creeptenuous.common.exceptions.messages.ExceptionDirectoryExistsMsg;
import com.zer0s2m.creeptenuous.common.http.ResponseCreateDirectoryApi;
import com.zer0s2m.creeptenuous.core.handlers.AtomicSystemCallManager;
import com.zer0s2m.creeptenuous.redis.services.security.OperationRights;
import com.zer0s2m.creeptenuous.services.redis.security.ServiceManagerRightsImpl;
import com.zer0s2m.creeptenuous.services.redis.system.ServiceCreateDirectoryRedisImpl;
import com.zer0s2m.creeptenuous.services.system.impl.ServiceCreateDirectoryImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

@V1APIRestController
public class ControllerApiCreateDirectory implements ControllerApiCreateDirectoryDoc {

    private final ServiceCreateDirectoryImpl createDirectory;

    private final ServiceCreateDirectoryRedisImpl serviceDirectoryRedis;

    /**
     * Service for managing user rights for interacting with a target file system object
     */
    private final ServiceManagerRightsImpl serviceManagerRights;

    @Autowired
    public ControllerApiCreateDirectory(
            ServiceCreateDirectoryImpl createDirectory,
            ServiceCreateDirectoryRedisImpl serviceDirectoryRedis,
            ServiceManagerRightsImpl serviceManagerRights
    ) {
        this.createDirectory = createDirectory;
        this.serviceDirectoryRedis = serviceDirectoryRedis;
        this.serviceManagerRights = serviceManagerRights;
    }

    /**
     * Create directory
     * <p>Called method via {@link AtomicSystemCallManager} - {@link ServiceCreateDirectoryImpl#create(List, String)}</p>
     * @param directoryForm directory create data
     * @param accessToken raw JWT access token
     * @return result create directory
     * @throws FileAlreadyExistsException file already exists
     * @throws InvocationTargetException Exception thrown by an invoked method or constructor.
     * @throws NoSuchMethodException Thrown when a particular method cannot be found.
     * @throws InstantiationException Thrown when an application tries to create an instance of a class
     * using the newInstance method in class {@code Class}.
     * @throws IllegalAccessException An IllegalAccessException is thrown when an application
     * tries to reflectively create an instance
     */
    @Override
    @PostMapping("/directory/create")
    @ResponseStatus(code = HttpStatus.CREATED)
    public final ResponseCreateDirectoryApi createDirectory(
            final @Valid @RequestBody DataCreateDirectoryApi directoryForm,
            @RequestHeader(name = "Authorization") String accessToken
    ) throws FileAlreadyExistsException, InvocationTargetException,
            NoSuchMethodException, InstantiationException, IllegalAccessException {
        serviceDirectoryRedis.setAccessToken(accessToken);
        serviceDirectoryRedis.checkRights(
                directoryForm.parents(),
                directoryForm.systemParents(),
                directoryForm.directoryName()
        );
        serviceManagerRights.setAccessToken(accessToken);

        ContainerDataCreateDirectory dataCreatedDirectory = AtomicSystemCallManager.call(
                this.createDirectory,
                directoryForm.systemParents(),
                directoryForm.directoryName()
        );

        serviceDirectoryRedis.create(dataCreatedDirectory);

        serviceManagerRights.addRight(serviceManagerRights.buildObj(
                dataCreatedDirectory.systemNameDirectory(),
                serviceManagerRights.getLoginUser(),
                OperationRights.ALL
        ));

        return new ResponseCreateDirectoryApi(
                dataCreatedDirectory.realNameDirectory(),
                dataCreatedDirectory.systemNameDirectory()
        );
    }

    @ExceptionHandler({FileAlreadyExistsException.class, java.nio.file.FileAlreadyExistsException.class})
    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
    public ExceptionDirectoryExistsMsg handleExceptionDirectoryExists(FileAlreadyExistsException error) {
        return new ExceptionDirectoryExistsMsg(error.getMessage());
    }
}
