package com.zer0s2m.CreepTenuous.services.directory.delete.services.impl;

import com.zer0s2m.CreepTenuous.services.core.ServiceFileSystem;
import com.zer0s2m.CreepTenuous.services.directory.delete.services.IServiceDeleteDirectory;
import com.zer0s2m.CreepTenuous.providers.build.os.services.impl.ServiceBuildDirectoryPath;
import com.zer0s2m.CreepTenuous.providers.build.os.services.CheckIsExistsDirectoryService;

import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;
import java.nio.file.Path;
import java.util.List;

@ServiceFileSystem("delete-directory")
public class ServiceDeleteDirectory implements IServiceDeleteDirectory, CheckIsExistsDirectoryService {
    private final ServiceBuildDirectoryPath buildDirectoryPath;

    @Autowired
    public ServiceDeleteDirectory(ServiceBuildDirectoryPath buildDirectoryPath) {
        this.buildDirectoryPath = buildDirectoryPath;
    }

    /**
     * Delete directory from system
     * @param systemParents parts of the system path - source
     * @param systemName system name directory
     * @throws NoSuchFileException system error
     */
    @Override
    public void delete(
            List<String> systemParents,
            String systemName
    ) throws NoSuchFileException {
        Path path = Paths.get(buildDirectoryPath.build(systemParents), systemName);
        checkDirectory(path);

        File removedDirectory = path.toFile();
        removedDirectory.delete();
    }
}
