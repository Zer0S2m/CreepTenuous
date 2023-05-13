package com.zer0s2m.creeptenuous.services.system.impl;

import com.zer0s2m.creeptenuous.common.annotations.ServiceFileSystem;
import com.zer0s2m.creeptenuous.services.system.ServiceDeleteFile;
import com.zer0s2m.creeptenuous.services.system.core.ServiceBuildDirectoryPath;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@ServiceFileSystem("service-delete-file")
public class ServiceDeleteFileImpl implements ServiceDeleteFile {
    private final ServiceBuildDirectoryPath buildDirectoryPath;

    @Autowired
    public ServiceDeleteFileImpl(ServiceBuildDirectoryPath buildDirectoryPath) {
        this.buildDirectoryPath = buildDirectoryPath;
    }

    /**
     * Delete file from file system
     * @param systemNameFile system name file
     * @param systemParents system path part directories
     * @return source path system
     * @throws IOException error system
     */
    @Override
    public Path delete(String systemNameFile, List<String> systemParents) throws IOException {
        Path pathFile = Paths.get(buildDirectoryPath.build(systemParents), systemNameFile);

        Files.delete(pathFile);

        return pathFile;
    }
}
