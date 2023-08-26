package com.zer0s2m.creeptenuous.common.exceptions;

/**
 * Custom category color scheme binding not found
 */
public class NotFoundUserColorCategoryException extends NotFoundException {

    public NotFoundUserColorCategoryException() {
        super("Custom category color scheme binding not found");
    }

}
