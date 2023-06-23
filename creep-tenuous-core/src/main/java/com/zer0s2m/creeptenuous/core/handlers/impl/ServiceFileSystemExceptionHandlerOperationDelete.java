package com.zer0s2m.creeptenuous.core.handlers.impl;

import com.zer0s2m.creeptenuous.core.annotations.AtomicFileSystemExceptionHandler;
import com.zer0s2m.creeptenuous.core.context.ContextAtomicFileSystem;
import com.zer0s2m.creeptenuous.core.context.ContextAtomicFileSystem.Operations;
import com.zer0s2m.creeptenuous.core.context.nio.file.FilesContextAtomic;
import com.zer0s2m.creeptenuous.core.handlers.ServiceFileSystemExceptionHandler;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Pattern;

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
    public void handleException(Throwable t, @NotNull HashMap<String, HashMap<String, Object>> operationsData) {
        operationsData.forEach((uniqueName, operationData) -> {
            Operations typeOperation = (Operations) operationData.get("operation");
            if (typeOperation.equals(Operations.DELETE)) {
                Path sourcePath = (Path) operationData.get("sourcePath");
                Path targetPath = (Path) operationData.get("targetPath");

                if (Files.exists(targetPath)) {
                    try {
                        prepare(sourcePath);

                        if (!Files.exists(sourcePath) && (boolean) operationData.get("isFile")) {
                            Files.move(targetPath, sourcePath, StandardCopyOption.REPLACE_EXISTING);
                        }

                        contextAtomicFileSystem.handleOperation(typeOperation, uniqueName);
                        logger.info(String.format(
                                "Moving a deleted file to the previous state: [%s] -> [%s]",
                                targetPath, sourcePath
                        ));
                    } catch (IOException e) {
                        contextAtomicFileSystem.handleOperation(typeOperation, uniqueName);
                        throw new RuntimeException(e);
                    }
                }
            }
        });
    }

    /**
     * Creates folders if they don't exist when moving a file
     * @param source if an I/O error occurs
     * @throws IOException the path to the file to move
     */
    protected void prepare(@NotNull Path source) throws IOException {
        List<String> partsSourcePath = List.of(source
                .toString()
                .replace(FilesContextAtomic.rootPath, "")
                .split(File.separator));

        AtomicReference<Path> restoredPartSourcePath =
                new AtomicReference<>(Path.of(FilesContextAtomic.rootPath));
        for (String partSourcePath : partsSourcePath) {
            restoredPartSourcePath.set(
                    Path.of(restoredPartSourcePath.toString(), partSourcePath)
            );
            if (!Files.exists(restoredPartSourcePath.get())
                    && partSourcePath.split(Pattern.quote(".")).length == 1) {
                Files.createDirectory(restoredPartSourcePath.get());
            }
        }
    }

}
