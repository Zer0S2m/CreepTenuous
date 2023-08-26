package com.zer0s2m.creeptenuous.core.atomic.handlers.impl;

import com.zer0s2m.creeptenuous.core.atomic.annotations.AtomicFileSystemExceptionHandler;
import com.zer0s2m.creeptenuous.core.atomic.context.ContextAtomicFileSystem;
import com.zer0s2m.creeptenuous.core.atomic.handlers.ServiceFileSystemExceptionHandler;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.nio.file.Path;

/**
 * The base interface for handling exceptions that interact with the file system.
 * Operation type handler {@link ContextAtomicFileSystem.Operations#DOWNLOAD}
 * <p>Used in conjunction with an annotation: {@link AtomicFileSystemExceptionHandler#handler()}</p>
 */
public class ServiceFileSystemExceptionHandlerOperationDownload implements ServiceFileSystemExceptionHandler {

    private final Logger logger = LoggerFactory.getLogger(ServiceFileSystemExceptionHandlerOperationDownload.class);

    /**
     * Context for working with the file system in <b>atomic mode</b>
     */
    private final ContextAtomicFileSystem contextAtomicFileSystem = ContextAtomicFileSystem.getInstance();

    /**
     * Handling an exception thrown by the file system. Each handler must be
     * responsible for one type of operation {@link ContextAtomicFileSystem.Operations}
     * <p>After handling the exception, call atomic mode handling
     * {@link ContextAtomicFileSystem#handleOperation(ContextAtomicFileSystem.Operations, String)}
     * to clear the context</p>
     *
     * @param t              exception
     * @param operationsData Data about the operation from the file system. Are in the context of atomic mode
     *                       {@link ContextAtomicFileSystem#getOperationsData()}
     */
    @Override
    public void handleException(Throwable t, @NotNull HashMap<String, HashMap<String, Object>> operationsData) {
        operationsData.forEach((uniqueName, operationData) -> {
            ContextAtomicFileSystem.Operations typeOperation = (ContextAtomicFileSystem.Operations) operationData.get("operation");
            if (typeOperation.equals(ContextAtomicFileSystem.Operations.DOWNLOAD)) {
                Path source = (Path) operationData.get("sourcePath");

                try {
                    logger.info(String.format(
                            "Delete zip file atomic mode [%s] : [%s]",
                            source, Files.deleteIfExists(source)
                    ));
                } catch (IOException e) {
                    contextAtomicFileSystem.handleOperation(typeOperation, uniqueName);
                    throw new RuntimeException(e);
                }

                contextAtomicFileSystem.handleOperation(typeOperation, uniqueName);
            }
        });
    }

}
