package com.zer0s2m.CreepTenuous.api.controllers.files.create;

import com.zer0s2m.CreepTenuous.api.controllers.files.create.data.DataCreateFile;
import com.zer0s2m.CreepTenuous.api.core.annotations.V1APIRestController;
import com.zer0s2m.CreepTenuous.providers.build.os.services.CheckIsExistsDirectoryApi;
import com.zer0s2m.CreepTenuous.providers.redis.controllers.CheckRightsActionFileSystem;
import com.zer0s2m.CreepTenuous.services.files.create.containers.ContainerDataCreatedFile;
import com.zer0s2m.CreepTenuous.services.files.create.exceptions.NotFoundTypeFileException;
import com.zer0s2m.CreepTenuous.services.files.create.exceptions.messages.FileAlreadyExistsMsg;
import com.zer0s2m.CreepTenuous.services.files.create.exceptions.messages.NotFoundTypeFileMsg;
import com.zer0s2m.CreepTenuous.services.files.create.services.impl.ServiceCreateFile;

import com.zer0s2m.CreepTenuous.services.files.create.services.impl.ServiceCreateFileRedis;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;

@V1APIRestController
public class ControllerApiCreateFile implements CheckIsExistsDirectoryApi, CheckRightsActionFileSystem {
    private final ServiceCreateFile serviceCreateFile;

    private final ServiceCreateFileRedis serviceFileRedis;

    @Autowired
    public ControllerApiCreateFile(ServiceCreateFile serviceCreateFile, ServiceCreateFileRedis serviceFileRedis) {
        this.serviceCreateFile = serviceCreateFile;
        this.serviceFileRedis = serviceFileRedis;
    }

    @PostMapping("/file/create")
    @ResponseStatus(code = HttpStatus.CREATED)
    public void createFile(
            final @Valid @RequestBody DataCreateFile file,
            @RequestHeader(name = "Authorization") String accessToken
    ) throws NotFoundTypeFileException, IOException {
        serviceFileRedis.setAccessToken(accessToken);
        serviceFileRedis.checkRights(file.parents(), file.systemParents(), null);
        ContainerDataCreatedFile dataCreatedFile = serviceCreateFile.create(
                file.systemParents(),
                file.nameFile(),
                file.typeFile()
        );
        serviceFileRedis.create(dataCreatedFile);
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
