package com.zer0s2m.creeptenuous.common.exceptions;

public class NotFoundUserColorException extends NotFoundException {

    public NotFoundUserColorException() {
        super("Color scheme not found for user");
    }

}
