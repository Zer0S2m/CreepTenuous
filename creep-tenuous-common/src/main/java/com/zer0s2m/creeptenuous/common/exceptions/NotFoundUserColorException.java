package com.zer0s2m.creeptenuous.common.exceptions;

/**
 * Not found user color entity
 */
public class NotFoundUserColorException extends NotFoundException {

    public NotFoundUserColorException() {
        super("Color scheme not found for user");
    }

}
