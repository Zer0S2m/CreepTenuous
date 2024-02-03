package com.zer0s2m.creeptenuous.core.atomic;

import com.zer0s2m.creeptenuous.core.balancer.FileBalancer;
import com.zer0s2m.creeptenuous.core.balancer.FileIsDirectoryException;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.util.Collection;
import java.util.List;
import java.nio.file.Path;
import java.util.HashMap;

/**
 * The base interface for handling exceptions that interact with the file system.
 * Operation type handler {@link ContextAtomicFileSystem.Operations#FRAGMENTATION}
 * <p>Used in conjunction with an annotation: {@link AtomicFileSystemExceptionHandler#handler()}</p>
 */
public class ServiceFileSystemExceptionHandlerOperationFragmentation implements ServiceFileSystemExceptionHandler {

    private final Logger logger = LoggerFactory.getLogger(
            ServiceFileSystemExceptionHandlerOperationFragmentation.class);

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
    @SuppressWarnings("unchecked")
    public void handleException(Throwable t, @NotNull HashMap<String, HashMap<String, Object>> operationsData) {
        operationsData.forEach((uniqueName, operationData) -> {
            ContextAtomicFileSystem.Operations typeOperation =
                    (ContextAtomicFileSystem.Operations) operationData.get("operation");
            if (typeOperation.equals(ContextAtomicFileSystem.Operations.FRAGMENTATION)) {
                Collection<Path> fragments = (Collection<Path>) operationData.get("fragments");
                Path source = (Path) operationData.get("sourcePath");

                if (fragments == null) {
                    logger.warn("Fragmented parts of the file were not found");
                } else {
                    List<Path> fragmentsList = fragments.stream().toList();

                    if (fragmentsList.isEmpty()) {
                        logger.warn("The fragmented file collection is empty");
                    } else {
                        if (source == null) {
                            logger.warn("Source file path was not found");
                        } else {
                            try {
                                logger.info(String.format(
                                        "Delete file atomic mode [%s] : [%s]. For fragmentation",
                                        source, Files.deleteIfExists(source)
                                ));
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        }

                        String[] splitFirstFragment = fragmentsList
                                .get(0)
                                .getFileName()
                                .toString()
                                .split("\\.");
                        if (splitFirstFragment.length == 3) {
                            source = Path.of(
                                    fragmentsList.get(0).getParent().toString(),
                                    splitFirstFragment[0] + "." + splitFirstFragment[1]
                            );
                        } else if (splitFirstFragment.length == 2) {
                            logger.warn(String.format(
                                    "One of the file fragments does not have an extension [%s]",
                                    fragmentsList.get(0)
                            ));
                            logger.info(String.format(
                                    "A new way to recover a file from its fragments [%s]",
                                    source
                            ));
                            source = Path.of(
                                    fragmentsList.get(0).getParent().toString(),
                                    splitFirstFragment[0] + "." + splitFirstFragment[1]
                            );
                        } else if (splitFirstFragment.length == 1) {
                            logger.warn(String.format(
                                    "One of the file fragments is broken [%s]",
                                    fragmentsList.get(0)
                            ));
                            source = Path.of(
                                    fragmentsList.get(0).getParent().toString(),
                                    splitFirstFragment[0]
                            );
                        }

                        logger.info(String.format(
                                "A new way to recover a file from its fragments [%s]",
                                source
                        ));

                        try {
                            Collection<Path> orderedFragments = FileBalancer.getAllParts(fragmentsList.get(0));
                            Path recoveredFile = FileBalancer.merge(orderedFragments, source);

                            logger.info(String.format(
                                    "Recovered file from fragments [%s]",
                                    recoveredFile
                            ));

                            orderedFragments.forEach(fragment -> {
                                try {
                                    logger.info(String.format(
                                            "Removing fragments of the recovered file [%s] : [%s]",
                                            fragment, Files.deleteIfExists(fragment)
                                    ));
                                } catch (IOException e) {
                                    throw new RuntimeException(e);
                                }
                            });
                        } catch (IOException | FileIsDirectoryException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }

                contextAtomicFileSystem.handleOperation(typeOperation, uniqueName);
            }
        });
    }

}
