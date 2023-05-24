package com.zer0s2m.creeptenuous.core.handlers.impl;

import com.zer0s2m.creeptenuous.core.annotations.AtomicFileSystemExceptionHandler;
import com.zer0s2m.creeptenuous.core.context.ContextAtomicFileSystem;
import com.zer0s2m.creeptenuous.core.context.ContextAtomicFileSystem.Operations;
import com.zer0s2m.creeptenuous.core.handlers.ServiceFileSystemExceptionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.nio.file.Path;

/**
 * The base interface for handling exceptions that interact with the file system.
 * Operation type handler {@link ContextAtomicFileSystem.Operations#DELETE}
 * <p>Used in conjunction with an annotation: {@link AtomicFileSystemExceptionHandler#handler()}</p>
 */
public class ServiceFileSystemExceptionHandlerOperationDelete implements ServiceFileSystemExceptionHandler {

    private final Logger logger = LoggerFactory.getLogger(ServiceFileSystemExceptionHandlerOperationDelete.class);

    /**
     * Context for working with the file system in <b>atomic mode</b>
     */
    private final ContextAtomicFileSystem contextAtomicFileSystem = ContextAtomicFileSystem.getInstance();

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
    @Override
    public void handleException(Throwable t, HashMap<String, HashMap<String, Object>> operationsData) {
        operationsData.forEach((uniqueName, operationData) -> {
            Operations typeOperation = (Operations) operationData.get("operation");
            if (typeOperation.equals(Operations.DELETE)) {
                Path sourcePath = (Path) operationData.get("sourcePath");
                Path targetPath = (Path) operationData.get("targetPath");

                if (Files.exists(targetPath)) {
                    try {
                        Files.move(targetPath, sourcePath, StandardCopyOption.REPLACE_EXISTING);
                        contextAtomicFileSystem.handleOperation(typeOperation, uniqueName);
                        logger.info(String.format(
                                "Moving a deleted file to the previous state: [%s] -> [%s]",
                                targetPath, sourcePath
                        ));
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });
    }
}
