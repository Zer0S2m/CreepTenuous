package com.zer0s2m.CreepTenuous.services.files.move.services.impl;

import com.zer0s2m.CreepTenuous.providers.build.os.services.impl.ServiceBuildDirectoryPath;
import com.zer0s2m.CreepTenuous.services.core.ServiceFileSystem;
import com.zer0s2m.CreepTenuous.services.files.move.services.IServiceMoveFile;

import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

import static java.nio.file.StandardCopyOption.ATOMIC_MOVE;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

@ServiceFileSystem("move-file")
public class ServiceMoveFile implements IServiceMoveFile {
    private final ServiceBuildDirectoryPath buildDirectoryPath;

    @Autowired
    public ServiceMoveFile(ServiceBuildDirectoryPath buildDirectoryPath) {
        this.buildDirectoryPath = buildDirectoryPath;
    }

    public Path move(String systemNameFile, List<String> systemParents, List<String> systemToParents)
            throws IOException {
        Path currentPath = Paths.get(buildDirectoryPath.build(systemParents), systemNameFile);
        Path createdNewPath = Paths.get(buildDirectoryPath.build(systemToParents), systemNameFile);

        return move(currentPath, createdNewPath);
    }

    public Path move(List<String> systemNameFiles, List<String> systemParents, List<String> systemToParents)
            throws IOException {
        List<Path> paths = new ArrayList<>();
        for (String nameFile : systemNameFiles) {
            paths.add(move(nameFile, systemParents, systemToParents));
        }
        return paths.get(0);
    }

    public Path move(Path source, Path target) throws IOException {
        return Files.move(source, target, ATOMIC_MOVE, REPLACE_EXISTING);
    }
}
