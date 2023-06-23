package com.zer0s2m.creeptenuous.core.handlers;

import com.zer0s2m.creeptenuous.core.annotations.AtomicFileSystemExceptionHandler;
import com.zer0s2m.creeptenuous.core.context.ContextAtomicFileSystem;

import java.util.HashMap;

/**
 * The base interface for handling exceptions that interact with the file system
 * for different operations {@link ContextAtomicFileSystem.Operations} use different handlers
 * <p>Used in conjunction with an annotation: {@link AtomicFileSystemExceptionHandler#handler()}</p>
 */
public interface ServiceFileSystemExceptionHandler {

    /**
     * Handling an exception thrown by the file system. Each handler must be
     * responsible for one type of operation {@link ContextAtomicFileSystem.Operations}
     * <p>After handling the exception, call atomic mode handling
     * {@link com.zer0s2m.creeptenuous.core.context.ContextAtomicFileSystem#handleOperation(ContextAtomicFileSystem.Operations, String)}
     * to clear the context</p>
     * @param t exception
     * @param operationsData Data about the operation from the file system. Are in the context of atomic mode
     *                       {@link com.zer0s2m.creeptenuous.core.context.ContextAtomicFileSystem#getOperationsData()}
     */
    void handleException(Throwable t, HashMap<String, HashMap<String, Object>> operationsData);

}
