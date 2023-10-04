package com.zer0s2m.creeptenuous.common.exceptions;

/**
 * Exceptions for loading an avatar for a user.
 */
public class UploadAvatarForUserException  extends RuntimeException {

    public UploadAvatarForUserException(String message) {
        super(message);
    }

    public UploadAvatarForUserException() {
        this("Sorry! Filename contains invalid path sequence");
    }

}
