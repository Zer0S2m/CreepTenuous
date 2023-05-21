package com.zer0s2m.creeptenuous.api.controllers.system;

import com.zer0s2m.creeptenuous.common.annotations.V1APIRestController;
import com.zer0s2m.creeptenuous.common.containers.ContainerDataCreateDirectory;
import com.zer0s2m.creeptenuous.common.data.DataCreateDirectoryApi;
import com.zer0s2m.creeptenuous.common.exceptions.FileAlreadyExistsException;
import com.zer0s2m.creeptenuous.common.exceptions.messages.ExceptionDirectoryExistsMsg;
import com.zer0s2m.creeptenuous.common.http.ResponseCreateDirectoryApi;
import com.zer0s2m.creeptenuous.core.handlers.AtomicSystemCallManager;
import com.zer0s2m.creeptenuous.redis.exceptions.NoRightsDirectoryException;
import com.zer0s2m.creeptenuous.services.redis.system.ServiceCreateDirectoryRedisImpl;
import com.zer0s2m.creeptenuous.services.system.impl.ServiceCreateDirectoryImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.InvocationTargetException;

@V1APIRestController
public class ControllerApiCreateDirectory {
    private final ServiceCreateDirectoryImpl createDirectory;

    private final ServiceCreateDirectoryRedisImpl serviceDirectoryRedis;

    @Autowired
    public ControllerApiCreateDirectory(
            ServiceCreateDirectoryImpl createDirectory,
            ServiceCreateDirectoryRedisImpl serviceDirectoryRedis
    ) {
        this.createDirectory = createDirectory;
        this.serviceDirectoryRedis = serviceDirectoryRedis;
    }

    @PostMapping("/directory/create")
    @ResponseStatus(code = HttpStatus.CREATED)
    public final ResponseCreateDirectoryApi createDirectory(
            final @Valid @RequestBody DataCreateDirectoryApi directoryForm,
            @RequestHeader(name = "Authorization") String accessToken
    ) throws FileAlreadyExistsException, NoRightsDirectoryException, InvocationTargetException,
            NoSuchMethodException, InstantiationException, IllegalAccessException {
        serviceDirectoryRedis.setAccessToken(accessToken);
        serviceDirectoryRedis.checkRights(directoryForm.parents(), directoryForm.systemParents(), directoryForm.name());
        ContainerDataCreateDirectory dataCreatedDirectory = AtomicSystemCallManager.call(
                this.createDirectory,
                directoryForm.systemParents(),
                directoryForm.name()
        );
        serviceDirectoryRedis.create(dataCreatedDirectory);
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
