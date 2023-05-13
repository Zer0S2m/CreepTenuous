package com.zer0s2m.creeptenuous.services.system.impl;

import com.zer0s2m.creeptenuous.common.annotations.ServiceFileSystem;
import com.zer0s2m.creeptenuous.services.system.ServiceDeleteDirectory;
import com.zer0s2m.creeptenuous.services.system.core.ServiceBuildDirectoryPath;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;
import java.nio.file.Path;
import java.util.List;

@ServiceFileSystem("delete-directory")
public class ServiceDeleteDirectoryImpl implements ServiceDeleteDirectory {
    private final ServiceBuildDirectoryPath buildDirectoryPath;

    @Autowired
    public ServiceDeleteDirectoryImpl(ServiceBuildDirectoryPath buildDirectoryPath) {
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

        File removedDirectory = path.toFile();
        removedDirectory.delete();
    }
}
