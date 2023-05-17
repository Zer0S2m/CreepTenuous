package com.zer0s2m.creeptenuous.core.handlers.impl;

import com.zer0s2m.creeptenuous.core.annotations.AtomicFileSystemExceptionHandler;
import com.zer0s2m.creeptenuous.core.handlers.ServiceFileSystemExceptionHandler;

/**
 * The base interface for handling exceptions that interact with the file system.
 * <p>Used in conjunction with an annotation: {@link AtomicFileSystemExceptionHandler#handler()}</p>
 */
public class ServiceFileSystemExceptionHandlerImpl implements ServiceFileSystemExceptionHandler {
    /**
     * Handle exception caused by file system
     *
     * @param t exception
     */
    @Override
    public void handleException(Throwable t) {
        System.out.println(t.getClass());
    }
}
