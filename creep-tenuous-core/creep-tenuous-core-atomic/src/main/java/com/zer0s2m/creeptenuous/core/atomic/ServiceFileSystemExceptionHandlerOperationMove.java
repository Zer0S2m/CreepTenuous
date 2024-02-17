package com.zer0s2m.creeptenuous.core.atomic;

import com.zer0s2m.creeptenuous.core.atomic.ContextAtomicFileSystem.Operations;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.nio.file.Path;

/**
 * The base interface for handling exceptions that interact with the file system.
 * Operation type handler {@link ContextAtomicFileSystem.Operations#MOVE}
 * <p>Used in conjunction with an annotation: {@link AtomicFileSystemExceptionHandler#handler()}</p>
 */
public class ServiceFileSystemExceptionHandlerOperationMove implements ServiceFileSystemExceptionHandler {

    private final Logger logger = LoggerFactory.getLogger(ServiceFileSystemExceptionHandlerOperationMove.class);

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
     * @param t exception
     * @param operationsData Data about the operation from the file system. Are in the context of atomic mode
     *                       {@link ContextAtomicFileSystem#getOperationsData()}
     */
    public void handleException(Throwable t, @NotNull HashMap<String, HashMap<String, Object>> operationsData) {
        operationsData.forEach((uniqueName, operationData) -> {
            Operations typeOperation = (Operations) operationData.get("operation");
            if (typeOperation.equals(Operations.MOVE)) {
                Path source = (Path) operationData.get("sourcePath");
                Path target = (Path) operationData.get("targetPath");

                if (Files.exists(target)) {
                    try {
                        Files.move(target, source, StandardCopyOption.ATOMIC_MOVE);
                        logger.info(String.format(
                                "Move file system object atomic mode [%s] to [%s] - true",
                                target, source
                        ));
                    } catch (IOException e) {
                        contextAtomicFileSystem.handleOperation(typeOperation, uniqueName);
                        throw new RuntimeException(e);
                    }
                } else {
                    logger.warn(String.format(
                            "Move file system object atomic mode [%s] - false (dile system object does not exist)",
                            target
                    ));
                }

                contextAtomicFileSystem.handleOperation(typeOperation, uniqueName);
            }
        });
    }

}
