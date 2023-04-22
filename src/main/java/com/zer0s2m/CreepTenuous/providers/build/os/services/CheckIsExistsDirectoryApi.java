package com.zer0s2m.CreepTenuous.providers.build.os.services;

import com.zer0s2m.CreepTenuous.services.directory.manager.exceptions.messages.ExceptionNotDirectoryMsg;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.nio.file.NoSuchFileException;

public interface CheckIsExistsDirectoryApi {
    @ExceptionHandler(NoSuchFileException.class)
    @ResponseStatus(code = HttpStatus.NOT_FOUND)
    default ExceptionNotDirectoryMsg handleExceptionNotDirectory(NoSuchFileException error) {
        return new ExceptionNotDirectoryMsg(error.getMessage());
    }
}
