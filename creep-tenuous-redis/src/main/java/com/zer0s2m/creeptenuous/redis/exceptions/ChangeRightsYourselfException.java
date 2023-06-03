package com.zer0s2m.creeptenuous.redis.exceptions;

/**
 * Change rights over the interaction of file system objects to itself
 */
public class ChangeRightsYourselfException extends Exception {

    public ChangeRightsYourselfException(String message) {
        super(message);
    }

    public ChangeRightsYourselfException() {
        this("You cannot add rights to yourself");
    }

}
