package com.zer0s2m.CreepTenuous.services.directory.delete.services.impl;

import com.zer0s2m.CreepTenuous.services.directory.manager.enums.Directory;
import com.zer0s2m.CreepTenuous.services.directory.delete.services.IDeleteDirectory;
import com.zer0s2m.CreepTenuous.providers.build.os.services.impl.BuildDirectoryPath;
import com.zer0s2m.CreepTenuous.providers.build.os.services.CheckIsExistsDirectoryService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;
import java.nio.file.Path;
import java.util.List;

@Service("delete-directory")
public class DeleteDirectory implements IDeleteDirectory, CheckIsExistsDirectoryService {
    private final BuildDirectoryPath buildDirectoryPath;

    @Autowired
    public DeleteDirectory(BuildDirectoryPath buildDirectoryPath) {
        this.buildDirectoryPath = buildDirectoryPath;
    }

    @Override
    public void delete(
            List<String> parents,
            String name
    ) throws NoSuchFileException {
        Path path = Paths.get(buildDirectoryPath.build(parents) + Directory.SEPARATOR.get() + name);
        checkDirectory(path);

        File removedDirectory = path.toFile();
        removedDirectory.delete();
    }
}
