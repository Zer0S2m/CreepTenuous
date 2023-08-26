package com.zer0s2m.creeptenuous.common.exceptions;

/**
 * The exception is for not found the user category
 */
public class NotFoundUserCategoryException extends NotFoundException {

    public NotFoundUserCategoryException(String message) {
        super(message);
    }

    public NotFoundUserCategoryException() {
        this("Not found category");
    }

}
