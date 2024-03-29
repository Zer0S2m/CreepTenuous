package com.zer0s2m.creeptenuous.services.system.impl;

import com.zer0s2m.creeptenuous.common.annotations.ServiceFileSystem;
import com.zer0s2m.creeptenuous.core.atomic.AtomicFileSystem;
import com.zer0s2m.creeptenuous.core.atomic.AtomicFileSystemExceptionHandler;
import com.zer0s2m.creeptenuous.core.atomic.CoreServiceFileSystem;
import com.zer0s2m.creeptenuous.core.atomic.ContextAtomicFileSystem;
import com.zer0s2m.creeptenuous.core.atomic.FilesContextAtomic;
import com.zer0s2m.creeptenuous.core.atomic.ServiceFileSystemExceptionHandlerOperationMove;
import com.zer0s2m.creeptenuous.services.system.ServiceMoveFile;
import com.zer0s2m.creeptenuous.services.system.core.ServiceBuildDirectoryPath;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

import static java.nio.file.StandardCopyOption.ATOMIC_MOVE;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

/**
 * File move service
 */
@ServiceFileSystem("service-move-file")
@CoreServiceFileSystem(method = "move")
public class ServiceMoveFileImpl implements ServiceMoveFile {

    private final Logger logger = LogManager.getLogger(ServiceMoveFile.class);

    private final ServiceBuildDirectoryPath buildDirectoryPath = new ServiceBuildDirectoryPath();

    /**
     * Move file(s)
     * @param systemNameFile system name file
     * @param systemParents parts of the system path - source
     * @param systemToParents parts of the system path - target
     * @return target
     * @throws IOException system error
     */
    @Override
    @AtomicFileSystem(
            name = "move-file",
            handlers = {
                    @AtomicFileSystemExceptionHandler(
                            handler = ServiceFileSystemExceptionHandlerOperationMove.class,
                            exception = NoSuchFileException.class,
                            operation = ContextAtomicFileSystem.Operations.MOVE
                    )
            }
    )
    public Path move(String systemNameFile, List<String> systemParents, List<String> systemToParents)
            throws IOException {
        Path currentPath = Paths.get(buildDirectoryPath.build(systemParents), systemNameFile);
        Path createdNewPath = Paths.get(buildDirectoryPath.build(systemToParents), systemNameFile);

        return move(currentPath, createdNewPath);
    }

    /**
     * Move files
     * @param systemNameFiles system names files
     * @param systemParents parts of the system path - source
     * @param systemToParents parts of the system path - target
     * @return target
     * @throws IOException system error
     */
    @Override
    @AtomicFileSystem(
            name = "move-file",
            handlers = {
                    @AtomicFileSystemExceptionHandler(
                            handler = ServiceFileSystemExceptionHandlerOperationMove.class,
                            exception = NoSuchFileException.class,
                            operation = ContextAtomicFileSystem.Operations.MOVE
                    )
            }
    )
    public Path move(@NotNull List<String> systemNameFiles, List<String> systemParents, List<String> systemToParents)
            throws IOException {
        List<Path> paths = new ArrayList<>();
        for (String nameFile : systemNameFiles) {
            paths.add(move(nameFile, systemParents, systemToParents));
        }
        return paths.get(0);
    }

    /**
     * Move file
     * @param source source system path
     * @param target target system path
     * @return target
     * @throws IOException system error
     */
    @Override
    @AtomicFileSystem(
            name = "move-file",
            handlers = {
                    @AtomicFileSystemExceptionHandler(
                            handler = ServiceFileSystemExceptionHandlerOperationMove.class,
                            exception = NoSuchFileException.class,
                            operation = ContextAtomicFileSystem.Operations.MOVE
                    )
            }
    )
    public Path move(Path source, Path target) throws IOException {
        logger.info(String.format(
                "Moving a file: source [%s] target [%s]",
                source, target
        ));

        return FilesContextAtomic.move(source, target, ATOMIC_MOVE, REPLACE_EXISTING);
    }

}
