package com.zer0s2m.creeptenuous.core.handlers.impl;

import com.zer0s2m.creeptenuous.core.annotations.AtomicFileSystemExceptionHandler;
import com.zer0s2m.creeptenuous.core.context.ContextAtomicFileSystem;
import com.zer0s2m.creeptenuous.core.handlers.ServiceFileSystemExceptionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.nio.file.Path;

/**
 * The base interface for handling exceptions that interact with the file system.
 * <p>Used in conjunction with an annotation: {@link AtomicFileSystemExceptionHandler#handler()}</p>
 */
public class ServiceFileSystemExceptionHandlerImpl implements ServiceFileSystemExceptionHandler {

    private final Logger logger = LoggerFactory.getLogger(ServiceFileSystemExceptionHandlerImpl.class);

    /**
     * Context for working with the file system in <b>atomic mode</b>
     */
    private final ContextAtomicFileSystem contextAtomicFileSystem = ContextAtomicFileSystem.getInstance();

    /**
     * Handle exception caused by file system.
     * <p>After handling the exception, call atomic mode handling
     * {@link com.zer0s2m.creeptenuous.core.context.ContextAtomicFileSystem#handleOperation(ContextAtomicFileSystem.Operations, String)}
     * to clear the context</p>
     * @param t exception
     * @param operationsData Data about the operation from the file system. Are in the context of atomic mode
     *                       {@link com.zer0s2m.creeptenuous.core.context.ContextAtomicFileSystem#getOperationsData()}
     */
    @Override
    public void handleException(Throwable t, HashMap<String, HashMap<String, Object>> operationsData) {
        operationsData.forEach((uniqueName, operationData) -> {
            Path systemPath = (Path) operationData.get("systemPath");
            try {
                logger.info(String.format(
                        "Delete file atomic mode [%s]: [%s]",
                        systemPath, Files.deleteIfExists(systemPath)
                ));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            contextAtomicFileSystem.handleOperation(
                    (ContextAtomicFileSystem.Operations) operationData.get("operation"),
                    uniqueName
            );
        });
    }
}
