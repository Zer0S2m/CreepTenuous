package com.zer0s2m.creeptenuous.core.handlers;

import com.zer0s2m.creeptenuous.core.annotations.AtomicFileSystemExceptionHandler;

import java.util.HashMap;

/**
 * The base interface for handling exceptions that interact with the file system.
 * <p>Used in conjunction with an annotation: {@link AtomicFileSystemExceptionHandler#handler()}</p>
 */
public interface ServiceFileSystemExceptionHandler {

    /**
     * Handle exception caused by file system
     * @param t exception
     * @param operationsData Data about the operation from the file system. Are in the context of atomic mode
     *                       {@link com.zer0s2m.creeptenuous.core.context.ContextAtomicFileSystem#getOperationsData()}
     */
    void handleException(Throwable t, HashMap<String, HashMap<String, Object>> operationsData);
}
