package com.zer0s2m.CreepTenuous.services.directory.move.services.impl;

import com.zer0s2m.CreepTenuous.services.directory.manager.enums.Directory;
import com.zer0s2m.CreepTenuous.services.directory.move.services.IMoveDirectory;
import com.zer0s2m.CreepTenuous.providers.build.os.services.impl.BuildDirectoryPath;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.List;

@Service("move-directory")
public class MoveDirectory implements IMoveDirectory {
    private final BuildDirectoryPath buildDirectoryPath;

    @Autowired
    public MoveDirectory(BuildDirectoryPath buildDirectoryPath) {
        this.buildDirectoryPath = buildDirectoryPath;
    }

    @Override
    public void move(
            List<String> parents,
            List<String> toParents,
            String nameDirectory
    ) throws IOException {
        Path currentPath = Paths.get(
                buildDirectoryPath.build(parents) + Directory.SEPARATOR.get() + nameDirectory
        );
        Paths.get(buildDirectoryPath.build(toParents));

        toParents.add(nameDirectory);
        Path createdNewPath = Paths.get(
                buildDirectoryPath.build(toParents)
        );

        File newDirectory = createdNewPath.toFile();
        if (!newDirectory.exists()) {
            newDirectory.mkdir();
        }

        Files.move(currentPath, createdNewPath, StandardCopyOption.REPLACE_EXISTING);
    }
}
