package com.zer0s2m.CreepTenuous.services.directory.delete.services.impl;

import com.zer0s2m.CreepTenuous.services.core.Directory;
import com.zer0s2m.CreepTenuous.services.directory.delete.services.IDeleteDirectory;
import com.zer0s2m.CreepTenuous.providers.build.os.services.impl.ServiceBuildDirectoryPath;
import com.zer0s2m.CreepTenuous.providers.build.os.services.CheckIsExistsDirectoryService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;
import java.nio.file.Path;
import java.util.List;

@Service("delete-directory")
public class ServiceDeleteDirectory implements IDeleteDirectory, CheckIsExistsDirectoryService {
    private final ServiceBuildDirectoryPath buildDirectoryPath;

    @Autowired
    public ServiceDeleteDirectory(ServiceBuildDirectoryPath buildDirectoryPath) {
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
