package com.zer0s2m.CreepTenuous.services.files.copy.services.impl;

import com.zer0s2m.CreepTenuous.providers.build.os.services.impl.BuildDirectoryPath;
import com.zer0s2m.CreepTenuous.services.directory.manager.enums.Directory;
import com.zer0s2m.CreepTenuous.services.files.copy.services.ICopyFile;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

@Service("copy-file")
public class CopyFile implements ICopyFile {
    private final BuildDirectoryPath buildDirectoryPath;

    @Autowired
    public CopyFile(BuildDirectoryPath buildDirectoryPath) {
        this.buildDirectoryPath = buildDirectoryPath;
    }

    @Override
    public void copy(String nameFile, List<String> parents, List<String> toParents) throws IOException {
        Path currentPath = Paths.get(
                Paths.get(buildDirectoryPath.build(parents)) + Directory.SEPARATOR.get() + nameFile
        );
        Path createdNewPath = Paths.get(
                Paths.get(buildDirectoryPath.build(toParents)) + Directory.SEPARATOR.get() + nameFile
        );

        copy(currentPath, createdNewPath);
    }

    @Override
    public void copy(List<String> nameFiles, List<String> parents, List<String> toParents) throws IOException {
        for (String name : nameFiles) {
            copy(name, parents, toParents);
        }
    }

    @Override
    public void copy(Path source, Path target) throws IOException {
        Files.copy(source, target, REPLACE_EXISTING);
    }
}
