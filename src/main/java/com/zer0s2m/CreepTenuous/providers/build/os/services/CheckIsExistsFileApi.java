package com.zer0s2m.CreepTenuous.providers.build.os.services;

import com.zer0s2m.CreepTenuous.api.controllers.common.exceptions.NoSuchFileExistsException;
import com.zer0s2m.CreepTenuous.api.controllers.common.exceptions.messages.NoSuchFileExists;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

public interface CheckIsExistsFileApi {
    @ExceptionHandler(NoSuchFileExistsException.class)
    @ResponseStatus(code = HttpStatus.NOT_FOUND)
    default NoSuchFileExists handleExceptionFileNotExists(NoSuchFileExistsException error) {
        return new NoSuchFileExists(error.getMessage());
    }
}
