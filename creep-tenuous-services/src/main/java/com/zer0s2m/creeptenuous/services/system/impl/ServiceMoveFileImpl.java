package com.zer0s2m.creeptenuous.services.system.impl;

import com.zer0s2m.creeptenuous.common.annotations.ServiceFileSystem;
import com.zer0s2m.creeptenuous.services.system.ServiceMoveFile;
import com.zer0s2m.creeptenuous.services.system.core.ServiceBuildDirectoryPath;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

import static java.nio.file.StandardCopyOption.ATOMIC_MOVE;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

@ServiceFileSystem("service-move-file")
public class ServiceMoveFileImpl implements ServiceMoveFile {
    private final ServiceBuildDirectoryPath buildDirectoryPath;

    @Autowired
    public ServiceMoveFileImpl(ServiceBuildDirectoryPath buildDirectoryPath) {
        this.buildDirectoryPath = buildDirectoryPath;
    }

    /**
     * Move file(s)
     * @param systemNameFile system name file
     * @param systemParents parts of the system path - source
     * @param systemToParents parts of the system path - target
     * @return target
     * @throws IOException system error
     */
    @Override
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
    public Path move(List<String> systemNameFiles, List<String> systemParents, List<String> systemToParents)
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
    public Path move(Path source, Path target) throws IOException {
        return Files.move(source, target, ATOMIC_MOVE, REPLACE_EXISTING);
    }
}
