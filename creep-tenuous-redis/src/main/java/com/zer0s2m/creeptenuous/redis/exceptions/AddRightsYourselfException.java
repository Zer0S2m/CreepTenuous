package com.zer0s2m.creeptenuous.redis.exceptions;

/**
 * Adding rights over the interaction of file system objects to itself
 */
public class AddRightsYourselfException extends Exception {

    public AddRightsYourselfException(String message) {
        super(message);
    }

    public AddRightsYourselfException() {
        this("You cannot add rights to yourself");
    }

}
