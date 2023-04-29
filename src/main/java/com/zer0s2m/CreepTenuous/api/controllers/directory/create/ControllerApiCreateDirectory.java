package com.zer0s2m.CreepTenuous.api.controllers.directory.create;

import com.zer0s2m.CreepTenuous.api.controllers.directory.create.data.FormCreateDirectoryApi;
import com.zer0s2m.CreepTenuous.api.core.version.v1.V1APIController;
import com.zer0s2m.CreepTenuous.providers.build.os.services.CheckIsExistsDirectoryApi;
import com.zer0s2m.CreepTenuous.services.directory.create.containers.ContainerDataCreatedDirectory;
import com.zer0s2m.CreepTenuous.services.directory.create.exceptions.FileAlreadyExistsException;
import com.zer0s2m.CreepTenuous.services.directory.create.exceptions.NoRightsCreateDirectoryException;
import com.zer0s2m.CreepTenuous.services.directory.create.exceptions.messages.ExceptionDirectoryExistsMsg;
import com.zer0s2m.CreepTenuous.services.directory.create.exceptions.messages.ExceptionNoRightsCreateDirectoryMsg;
import com.zer0s2m.CreepTenuous.services.directory.create.services.impl.ServiceCreateDirectory;
import com.zer0s2m.CreepTenuous.services.directory.create.services.impl.ServiceCreateDirectoryRedis;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.nio.file.NoSuchFileException;

@V1APIController
public class ControllerApiCreateDirectory implements CheckIsExistsDirectoryApi {
    private final ServiceCreateDirectory createDirectory;

    private final ServiceCreateDirectoryRedis serviceDirectoryRedis;

    @PostMapping("/directory/create")
    @ResponseStatus(code = HttpStatus.CREATED)
    public final void createDirectory(
            final @Valid @RequestBody FormCreateDirectoryApi directoryForm,
            @RequestHeader(name = "Authorization") String accessToken
    ) throws NoSuchFileException, FileAlreadyExistsException, NoRightsCreateDirectoryException,
            java.nio.file.FileAlreadyExistsException {
        serviceDirectoryRedis.setAccessToken(accessToken);
        serviceDirectoryRedis.checkRights(directoryForm.parents(), directoryForm.systemParents(), directoryForm.name());
        ContainerDataCreatedDirectory dataCreatedDirectory = createDirectory.create(
                directoryForm.systemParents(),
                directoryForm.name()
        );
        serviceDirectoryRedis.create(dataCreatedDirectory);
    }

    @Autowired
    public ControllerApiCreateDirectory(
            ServiceCreateDirectory createDirectory,
            ServiceCreateDirectoryRedis serviceDirectoryRedis
    ) {
        this.createDirectory = createDirectory;
        this.serviceDirectoryRedis = serviceDirectoryRedis;
    }

    @ExceptionHandler({FileAlreadyExistsException.class, java.nio.file.FileAlreadyExistsException.class})
    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
    public ExceptionDirectoryExistsMsg handleExceptionDirectoryExists(FileAlreadyExistsException error) {
        return new ExceptionDirectoryExistsMsg(error.getMessage());
    }

    @ExceptionHandler(NoRightsCreateDirectoryException.class)
    @ResponseStatus(code = HttpStatus.FORBIDDEN)
    public ExceptionNoRightsCreateDirectoryMsg handleExceptionNoRightsCreateDirectory(
            NoRightsCreateDirectoryException error
    ) {
        return new ExceptionNoRightsCreateDirectoryMsg(error.getMessage());
    }
}
