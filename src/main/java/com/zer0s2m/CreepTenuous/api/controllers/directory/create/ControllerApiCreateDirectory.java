package com.zer0s2m.CreepTenuous.api.controllers.directory.create;

import com.zer0s2m.CreepTenuous.api.controllers.directory.create.data.FormCreateDirectoryApi;
import com.zer0s2m.CreepTenuous.api.core.version.v1.V1APIController;
import com.zer0s2m.CreepTenuous.services.directory.create.exceptions.messages.ExceptionDirectoryExistsMsg;
import com.zer0s2m.CreepTenuous.services.directory.create.services.impl.ServiceCreateDirectory;
import com.zer0s2m.CreepTenuous.providers.build.os.services.CheckIsExistsDirectoryApi;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;

import java.nio.file.FileAlreadyExistsException;
import java.nio.file.NoSuchFileException;

@V1APIController
public class ControllerApiCreateDirectory implements CheckIsExistsDirectoryApi {
    private final ServiceCreateDirectory createDirectory;

    @PostMapping("/directory/create")
    @ResponseStatus(code = HttpStatus.CREATED)
    public final void createDirectory(
            final @Valid @RequestBody FormCreateDirectoryApi directoryForm
    ) throws NoSuchFileException, FileAlreadyExistsException {
        createDirectory.create(
                directoryForm.parents(),
                directoryForm.name()
        );
    }

    @Autowired
    public ControllerApiCreateDirectory(ServiceCreateDirectory createDirectory) {
        this.createDirectory = createDirectory;
    }

    @ExceptionHandler(FileAlreadyExistsException.class)
    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
    public ExceptionDirectoryExistsMsg handleExceptionDirectoryExists(FileAlreadyExistsException error) {
        return new ExceptionDirectoryExistsMsg(error.getMessage());
    }
}
