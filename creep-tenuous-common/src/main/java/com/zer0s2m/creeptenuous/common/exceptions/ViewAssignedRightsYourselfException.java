package com.zer0s2m.creeptenuous.common.exceptions;

/**
 * Exception for obtaining information about assigned rights
 * to a file object if the owner is the current user.
 */
public class ViewAssignedRightsYourselfException extends Exception {

    public ViewAssignedRightsYourselfException(String message) {
        super(message);
    }

}
