package com.zer0s2m.creeptenuous.api.controllers.system;

import com.zer0s2m.creeptenuous.common.annotations.V1APIRestController;
import com.zer0s2m.creeptenuous.common.containers.ContainerDataCreateFile;
import com.zer0s2m.creeptenuous.common.data.DataCreateFileApi;
import com.zer0s2m.creeptenuous.common.exceptions.NotFoundTypeFileException;
import com.zer0s2m.creeptenuous.common.exceptions.messages.FileAlreadyExistsMsg;
import com.zer0s2m.creeptenuous.common.exceptions.messages.NotFoundTypeFileMsg;
import com.zer0s2m.creeptenuous.common.http.ResponseCreateFileApi;
import com.zer0s2m.creeptenuous.services.redis.system.ServiceCreateFileRedisImpl;
import com.zer0s2m.creeptenuous.services.system.impl.ServiceCreateFileImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;

@V1APIRestController
public class ControllerApiCreateFile {
    private final ServiceCreateFileImpl serviceCreateFile;

    private final ServiceCreateFileRedisImpl serviceFileRedis;

    @Autowired
    public ControllerApiCreateFile(
            ServiceCreateFileImpl serviceCreateFile,
            ServiceCreateFileRedisImpl serviceFileRedis
    ) {
        this.serviceCreateFile = serviceCreateFile;
        this.serviceFileRedis = serviceFileRedis;
    }

    @PostMapping("/file/create")
    @ResponseStatus(code = HttpStatus.CREATED)
    public ResponseCreateFileApi createFile(
            final @Valid @RequestBody DataCreateFileApi file,
            @RequestHeader(name = "Authorization") String accessToken
    ) throws NotFoundTypeFileException, IOException {
        serviceFileRedis.setAccessToken(accessToken);
        serviceFileRedis.checkRights(file.parents(), file.systemParents(), null);
        ContainerDataCreateFile dataCreatedFile = serviceCreateFile.create(
                file.systemParents(),
                file.nameFile(),
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
