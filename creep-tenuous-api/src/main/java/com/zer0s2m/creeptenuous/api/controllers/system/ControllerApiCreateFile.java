package com.zer0s2m.creeptenuous.api.controllers.system;

import com.zer0s2m.creeptenuous.api.documentation.controllers.ControllerApiCreateFileDoc;
import com.zer0s2m.creeptenuous.common.annotations.V1APIRestController;
import com.zer0s2m.creeptenuous.common.containers.ContainerDataCreateFile;
import com.zer0s2m.creeptenuous.common.data.DataCreateFileApi;
import com.zer0s2m.creeptenuous.common.enums.OperationRights;
import com.zer0s2m.creeptenuous.common.exceptions.NotFoundTypeFileException;
import com.zer0s2m.creeptenuous.common.exceptions.messages.FileAlreadyExistsMsg;
import com.zer0s2m.creeptenuous.common.exceptions.messages.NotFoundTypeFileMsg;
import com.zer0s2m.creeptenuous.common.http.ResponseCreateFileApi;
import com.zer0s2m.creeptenuous.core.handlers.AtomicSystemCallManager;
import com.zer0s2m.creeptenuous.redis.services.security.ServiceManagerRights;
import com.zer0s2m.creeptenuous.services.redis.system.ServiceCreateFileRedisImpl;
import com.zer0s2m.creeptenuous.services.system.impl.ServiceCreateFileImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.InvocationTargetException;
import java.nio.file.FileAlreadyExistsException;
import java.util.List;

@V1APIRestController
public class ControllerApiCreateFile implements ControllerApiCreateFileDoc {

    static final OperationRights operationRights = OperationRights.CREATE;

    private final ServiceCreateFileImpl serviceCreateFile;

    private final ServiceCreateFileRedisImpl serviceFileRedis;

    private final ServiceManagerRights serviceManagerRights;

    @Autowired
    public ControllerApiCreateFile(
            ServiceCreateFileImpl serviceCreateFile,
            ServiceCreateFileRedisImpl serviceFileRedis,
            ServiceManagerRights serviceManagerRights
    ) {
        this.serviceCreateFile = serviceCreateFile;
        this.serviceFileRedis = serviceFileRedis;
        this.serviceManagerRights = serviceManagerRights;
    }

    /**
     * Create file
     * <p>Called method via {@link AtomicSystemCallManager} - {@link ServiceCreateFileImpl#create(List, String, Integer)}</p>
     * @param file file create data
     * @param accessToken raw JWT access token
     * @return result create file
     * @throws NoSuchMethodException Thrown when a particular method cannot be found.
     * @throws InstantiationException Thrown when an application tries to create an instance of a class
     * using the newInstance method in class {@code Class}.
     * @throws IllegalAccessException An IllegalAccessException is thrown when an application
     * tries to reflectively create an instance
     */
    @Override
    @PostMapping("/file/create")
    @ResponseStatus(code = HttpStatus.CREATED)
    public ResponseCreateFileApi createFile(
            final @Valid @RequestBody DataCreateFileApi file,
            @RequestHeader(name = "Authorization") String accessToken
    ) throws InvocationTargetException, NoSuchMethodException, IllegalAccessException, InstantiationException {
        serviceFileRedis.setAccessToken(accessToken);
        boolean isRights = serviceFileRedis.checkRights(file.parents(), file.systemParents(), null, false);

        if (!isRights) {
            serviceManagerRights.setAccessClaims(accessToken);
            serviceManagerRights.setIsWillBeCreated(false);
            serviceManagerRights.checkRightsByOperation(operationRights, file.systemParents());
        }

        ContainerDataCreateFile dataCreatedFile = AtomicSystemCallManager.call(
                this.serviceCreateFile,
                file.systemParents(),
                file.fileName(),
                file.typeFile()
        );
        serviceFileRedis.create(dataCreatedFile);
        return new ResponseCreateFileApi(dataCreatedFile.realNameFile(), dataCreatedFile.systemNameFile());
    }

    @ExceptionHandler(NotFoundTypeFileException.class)
    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
    public NotFoundTypeFileMsg handleExceptionNotFoundTypeFile(NotFoundTypeFileException error) {
        return new NotFoundTypeFileMsg(error.getMessage());
    }

    @ExceptionHandler(FileAlreadyExistsException.class)
    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
    public FileAlreadyExistsMsg handleExceptionFileExists(FileAlreadyExistsException error) {
        return new FileAlreadyExistsMsg(error.getMessage());
    }
}