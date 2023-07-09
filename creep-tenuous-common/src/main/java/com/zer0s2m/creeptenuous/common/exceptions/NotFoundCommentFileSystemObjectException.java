package com.zer0s2m.creeptenuous.common.exceptions;

/**
 * The exception is for not found comments for filesystem objects
 */
public class NotFoundCommentFileSystemObjectException extends NotFoundException {

    public NotFoundCommentFileSystemObjectException(String message) {
        super(message);
    }

    public NotFoundCommentFileSystemObjectException() {
        this("Not found comment");
    }

}
