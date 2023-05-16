package com.zer0s2m.creeptenuous.core.services;

import com.zer0s2m.creeptenuous.core.annotations.AtomicFileSystemExceptionHandler;

/**
 * The base interface for handling exceptions that interact with the file system.
 * <p>Used in conjunction with an annotation: {@link AtomicFileSystemExceptionHandler#handler()}</p>
 */
public interface ServiceFileSystemExceptionHandler {
    /**
     * Handle exception caused by file system
     * @param t exception
     */
    void handleException(Throwable t);
}
