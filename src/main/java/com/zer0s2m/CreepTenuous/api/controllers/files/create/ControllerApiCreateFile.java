package com.zer0s2m.CreepTenuous.api.controllers.files.create;

import com.zer0s2m.CreepTenuous.api.controllers.files.create.data.DataCreateFile;
import com.zer0s2m.CreepTenuous.api.core.version.v1.V1APIController;
import com.zer0s2m.CreepTenuous.providers.build.os.services.CheckIsExistsDirectoryApi;
import com.zer0s2m.CreepTenuous.services.files.create.exceptions.NotFoundTypeFileException;
import com.zer0s2m.CreepTenuous.services.files.create.exceptions.messages.FileAlreadyExistsMsg;
import com.zer0s2m.CreepTenuous.services.files.create.exceptions.messages.NotFoundTypeFileMsg;
import com.zer0s2m.CreepTenuous.services.files.create.services.impl.ServiceCreateFile;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;

@V1APIController
public class ControllerApiCreateFile implements CheckIsExistsDirectoryApi {
    private final ServiceCreateFile serviceCreateFile;

    @Autowired
    public ControllerApiCreateFile(ServiceCreateFile serviceCreateFile) {
        this.serviceCreateFile = serviceCreateFile;
    }

    @PostMapping("/file/create")
    @ResponseStatus(code = HttpStatus.CREATED)
    public void createFile(
            final @Valid @RequestBody DataCreateFile file
    ) throws NotFoundTypeFileException, IOException {
        serviceCreateFile.create(
                file.parents(),
                file.nameFile(),
                file.typeFile()
        );
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
