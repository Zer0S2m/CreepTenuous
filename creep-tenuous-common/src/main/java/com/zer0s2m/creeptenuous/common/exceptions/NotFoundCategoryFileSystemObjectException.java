package com.zer0s2m.creeptenuous.common.exceptions;

/**
 * No associated file object found for custom category
 */
public class NotFoundCategoryFileSystemObjectException extends NotFoundException {

    public NotFoundCategoryFileSystemObjectException(String message) {
        super(message);
    }

    public NotFoundCategoryFileSystemObjectException() {
        this("No associated file object found for custom category");
    }

}
