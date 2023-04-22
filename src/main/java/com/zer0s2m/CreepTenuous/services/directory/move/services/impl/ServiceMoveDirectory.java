package com.zer0s2m.CreepTenuous.services.directory.move.services.impl;

import com.zer0s2m.CreepTenuous.components.RootPath;
import com.zer0s2m.CreepTenuous.services.directory.move.enums.MethodMoveDirectory;
import com.zer0s2m.CreepTenuous.services.directory.move.services.IMoveDirectory;
import com.zer0s2m.CreepTenuous.providers.build.os.services.impl.ServiceBuildDirectoryPath;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.Objects;

@Service("move-directory")
public class ServiceMoveDirectory implements IMoveDirectory {
    private final ServiceBuildDirectoryPath buildDirectoryPath;
    private final RootPath rootPath;

    @Autowired
    public ServiceMoveDirectory(ServiceBuildDirectoryPath buildDirectoryPath, RootPath rootPath) {
        this.buildDirectoryPath = buildDirectoryPath;
        this.rootPath = rootPath;
    }

    @Override
    public void move(
            List<String> parents,
            List<String> toParents,
            String nameDirectory,
            Integer method
    ) throws IOException {
        Path currentPathParents = Paths.get(buildDirectoryPath.build(parents));
        Path currentPath = Path.of(currentPathParents.toString(), nameDirectory);
        Path createdNewPath = builderDirectory(toParents, nameDirectory, method);
        Files.move(currentPath, createdNewPath, StandardCopyOption.REPLACE_EXISTING);
    }

    protected Path builderDirectory(List<String> toParents, String nameDirectory, Integer method) throws IOException {
        Path newPathDirectory = Path.of(rootPath.getRootPath());
        for (String partPath : toParents) {
            newPathDirectory = Path.of(newPathDirectory.toString(), partPath);
        }

        if (Objects.equals(method, MethodMoveDirectory.FOLDER.getMethod())) {
            newPathDirectory = Path.of(newPathDirectory.toString(), nameDirectory);
            if (!Files.exists(newPathDirectory)) {
                Files.createDirectories(newPathDirectory);
            }
        }

        return newPathDirectory;
    }
}
