package com.zer0s2m.CreepTenuous.providers.redis.controllers;

import com.zer0s2m.CreepTenuous.services.core.exceptions.NoRightsCreateDirectoryException;
import com.zer0s2m.CreepTenuous.services.core.exceptions.messages.ExceptionNoRightsCreateDirectoryMsg;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

public interface CheckRightsActionFileSystem {
    @ExceptionHandler(NoRightsCreateDirectoryException.class)
    @ResponseStatus(code = HttpStatus.FORBIDDEN)
    default ExceptionNoRightsCreateDirectoryMsg handleExceptionNoRightsCreateDirectory(
            NoRightsCreateDirectoryException error
    ) {
        return new ExceptionNoRightsCreateDirectoryMsg(error.getMessage());
    }
}
