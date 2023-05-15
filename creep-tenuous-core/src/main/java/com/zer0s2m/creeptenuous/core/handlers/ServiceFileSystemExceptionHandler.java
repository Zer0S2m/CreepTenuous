package com.zer0s2m.creeptenuous.core.handlers;

/**
 * Basic interface for handling exceptions that interact with the file system
 */
public interface ServiceFileSystemExceptionHandler {
    /**
     * Handle exception caused by file system
     * @param t exception
     */
    void handleException(Throwable t);
}
