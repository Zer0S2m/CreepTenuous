package com.zer0s2m.CreepTenuous.services.files.move.services.impl;

import com.zer0s2m.CreepTenuous.providers.build.os.services.impl.ServiceBuildDirectoryPath;
import com.zer0s2m.CreepTenuous.services.directory.manager.enums.Directory;
import com.zer0s2m.CreepTenuous.services.files.move.services.IMoveFile;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;

import static java.nio.file.StandardCopyOption.ATOMIC_MOVE;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

@Service("move-file")
public class ServiceMoveFile implements IMoveFile {
    private final ServiceBuildDirectoryPath buildDirectoryPath;

    @Autowired
    public ServiceMoveFile(ServiceBuildDirectoryPath buildDirectoryPath) {
        this.buildDirectoryPath = buildDirectoryPath;
    }

    public void move(String nameFile, List<String> parents, List<String> toParents) throws IOException {
        Path currentPath = Paths.get(
                Paths.get(buildDirectoryPath.build(parents)) + Directory.SEPARATOR.get() + nameFile
        );
        Path createdNewPath = Paths.get(
                Paths.get(buildDirectoryPath.build(toParents)) + Directory.SEPARATOR.get() + nameFile
        );

        move(currentPath, createdNewPath);
    }

    public void move(List<String> nameFiles, List<String> parents, List<String> toParents) throws IOException {
        for (String nameFile : nameFiles) {
            move(nameFile, parents, toParents);
        }
    }

    public void move(Path source, Path target) throws IOException {
        Files.move(source, target, ATOMIC_MOVE, REPLACE_EXISTING);
    }
}
