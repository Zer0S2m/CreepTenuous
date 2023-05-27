package com.zer0s2m.creeptenuous.services.system.impl;

import com.zer0s2m.creeptenuous.common.annotations.ServiceFileSystem;
import com.zer0s2m.creeptenuous.common.components.RootPath;
import com.zer0s2m.creeptenuous.common.containers.ContainerInfoFileSystemObject;
import com.zer0s2m.creeptenuous.common.enums.Directory;
import com.zer0s2m.creeptenuous.common.enums.MethodCopyDirectory;
import com.zer0s2m.creeptenuous.core.annotations.AtomicFileSystem;
import com.zer0s2m.creeptenuous.core.annotations.AtomicFileSystemExceptionHandler;
import com.zer0s2m.creeptenuous.core.annotations.CoreServiceFileSystem;
import com.zer0s2m.creeptenuous.core.context.ContextAtomicFileSystem;
import com.zer0s2m.creeptenuous.core.context.nio.file.FilesContextAtomic;
import com.zer0s2m.creeptenuous.core.handlers.impl.ServiceFileSystemExceptionHandlerOperationCopy;
import com.zer0s2m.creeptenuous.core.services.AtomicServiceFileSystem;
import com.zer0s2m.creeptenuous.services.system.utils.WalkDirectoryInfo;
import com.zer0s2m.creeptenuous.services.system.ServiceCopyDirectory;
import com.zer0s2m.creeptenuous.core.services.Distribution;
import com.zer0s2m.creeptenuous.services.system.core.ServiceBuildDirectoryPath;
import com.zer0s2m.creeptenuous.services.system.utils.UtilsFiles;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Stream;

@ServiceFileSystem("service-copy-directory")
@CoreServiceFileSystem(method = "copy")
public class ServiceCopyDirectoryImpl implements ServiceCopyDirectory, AtomicServiceFileSystem {
    private final ServiceBuildDirectoryPath buildDirectoryPath;

    private final String rootPath;

    private Path target;

    /**
     * Context for working with the file system in <b>atomic mode</b>
     */
    private final ContextAtomicFileSystem contextAtomicFileSystem = ContextAtomicFileSystem.getInstance();

    /**
     * <p>Parts of source paths on target</p>
     * <b>Key</b> - old source system path<br>
     * <b>Value</b> - new target system path
     */
    private final HashMap<String, String> paths = new HashMap<>();

    @Autowired
    public ServiceCopyDirectoryImpl(ServiceBuildDirectoryPath buildDirectoryPath, RootPath rootPath) {
        this.buildDirectoryPath = buildDirectoryPath;
        this.rootPath = rootPath.getRootPath();
    }

    /**
     * Copy directory
     * @param systemParents system path part directories
     * @param systemToParents system path part directories
     * @param systemNameDirectory system name directory
     * @param method method copy {@link MethodCopyDirectory}
     * @return information about copied file system objects
     * @throws IOException error system
     */
    @Override
    @AtomicFileSystem(
            name = "copy-directory",
            handlers = {
                    @AtomicFileSystemExceptionHandler(
                            exception = IOException.class,
                            handler = ServiceFileSystemExceptionHandlerOperationCopy.class,
                            operation = ContextAtomicFileSystem.Operations.COPY
                    )
            }
    )
    public List<ContainerInfoFileSystemObject> copy(
            List<String> systemParents,
            List<String> systemToParents,
            String systemNameDirectory,
            Integer method
    ) throws IOException {
        Path source = Paths.get(buildDirectoryPath.build(systemParents), systemNameDirectory);

        if (Objects.equals(method, MethodCopyDirectory.FOLDER.getMethod())) {
            this.target = Paths.get(buildDirectoryPath.build(systemToParents));
        } else {
            this.target = Paths.get(buildDirectoryPath.build(systemToParents));
        }

        List<ContainerInfoFileSystemObject> attached = WalkDirectoryInfo.walkDirectory(source, this.target);

        try (Stream<Path> stream = Files.walk(source)) {
            stream.forEach(targetWalk -> {
                try {
                    buildingPaths(targetWalk);
                    Path newTarget = getTarget(targetWalk, method);

                    if (!Files.exists(newTarget)) {
                        FilesContextAtomic.copy(
                                targetWalk,
                                newTarget,
                                StandardCopyOption.REPLACE_EXISTING
                        );
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        }

        return attached;
    }

    /**
     * Building paths {@link ServiceCopyDirectoryImpl#paths}
     * @param source source system file object
     */
    private void buildingPaths(Path source) {
        String sourceStr = source.toString().replace(rootPath + Directory.SEPARATOR.get(), "");
        List<String> splitSource = Arrays.asList(sourceStr.split(Directory.SEPARATOR.get()));

        splitSource.forEach((part) -> {
            if (!this.paths.containsKey(part)) {
                if (Files.isRegularFile(source)) {
                    this.paths.put(part, UtilsFiles.getNewFileName(part));
                } else {
                    this.paths.put(part, Distribution.getUUID());
                }
            }
        });
    }

    /**
     * Get parts system names for target
     * @param source source system file object
     * @return parts system names for building path
     */
    private Path getTarget(Path source, Integer method) {
        List<String> parts = new ArrayList<>();
        String sourceStr = source.toString().replace(rootPath + Directory.SEPARATOR.get(), "");
        List<String> splitSource = Arrays.asList(sourceStr.split(Directory.SEPARATOR.get()));

        splitSource.forEach((part) -> parts.add(this.paths.get(part)));

        if (method.equals(MethodCopyDirectory.CONTENT.getMethod())) {
            parts.remove(0);
        }

        return Paths.get(String.valueOf(this.target), String.join(Directory.SEPARATOR.get(), parts));
    }
}
