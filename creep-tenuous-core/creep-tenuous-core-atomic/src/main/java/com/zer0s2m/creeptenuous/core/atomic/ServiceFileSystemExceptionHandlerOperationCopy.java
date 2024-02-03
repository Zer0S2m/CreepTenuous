package com.zer0s2m.creeptenuous.core.atomic;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Stream;

/**
 * The base interface for handling exceptions that interact with the file system.
 * Operation type handler {@link ContextAtomicFileSystem.Operations#COPY}
 * <p>Used in conjunction with an annotation: {@link AtomicFileSystemExceptionHandler#handler()}</p>
 */
public class ServiceFileSystemExceptionHandlerOperationCopy implements ServiceFileSystemExceptionHandler {

    private final Logger logger = LoggerFactory.getLogger(ServiceFileSystemExceptionHandlerOperationCopy.class);

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
        AtomicBoolean isEmpty = new AtomicBoolean(false);

        operationsData.forEach((uniqueName, operationData) -> {
            ContextAtomicFileSystem.Operations typeOperation = (ContextAtomicFileSystem.Operations) operationData.get("operation");
            if (typeOperation.equals(ContextAtomicFileSystem.Operations.COPY)) {
                Path targetPath = (Path) operationData.get("targetPath");
                try {
                    if (Files.isDirectory(targetPath)) {
                        try (Stream<Path> pathStream = Files.walk(targetPath)) {
                            pathStream
                                    .sorted(Comparator.reverseOrder())
                                    .forEach(path -> {
                                        try {
                                            logger.info(String.format(
                                                    "Delete a file atomic mode [%s]: [%s]",
                                                    targetPath, Files.deleteIfExists(path)
                                            ));
                                        } catch (IOException e) {
                                            throw new RuntimeException(e);
                                        }
                                    });
                        }
                    } else {
                        logger.info(String.format(
                                "Delete a file atomic mode [%s]: [%s]",
                                targetPath, Files.deleteIfExists(targetPath)
                        ));
                    }
                    contextAtomicFileSystem.handleOperation(typeOperation, uniqueName);
                    isEmpty.set(true);
                } catch (DirectoryNotEmptyException e) {
                    logger.warn(e.toString());
                } catch (IOException e) {
                    logger.error(e.toString());
                }
            }
        });

        if (!isEmpty.get() && !operationsData.isEmpty()) {
            handleException(t, operationsData);
        }
    }

}
