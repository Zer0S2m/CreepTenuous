package com.zer0s2m.CreepTenuous.services.files.delete.services.impl;

import com.zer0s2m.CreepTenuous.providers.build.os.services.impl.ServiceBuildDirectoryPath;
import com.zer0s2m.CreepTenuous.services.core.ServiceFileSystem;
import com.zer0s2m.CreepTenuous.services.files.delete.services.IDeleteFile;
import com.zer0s2m.CreepTenuous.api.controllers.common.exceptions.NoSuchFileExistsException;
import com.zer0s2m.CreepTenuous.providers.build.os.services.CheckIsExistsFileService;

import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@ServiceFileSystem("delete-file")
public class ServiceDeleteFile implements IDeleteFile, CheckIsExistsFileService {
    private final ServiceBuildDirectoryPath buildDirectoryPath;

    @Autowired
    public ServiceDeleteFile(ServiceBuildDirectoryPath buildDirectoryPath) {
        this.buildDirectoryPath = buildDirectoryPath;
    }

    /**
     * Delete file from file system
     * @param systemNameFile system name file
     * @param systemParents system path part directories
     * @return source path system
     * @throws IOException error system
     * @throws NoSuchFileExistsException when no file in file system
     */
    @Override
    public Path delete(String systemNameFile, List<String> systemParents)
            throws IOException, NoSuchFileExistsException {
        Path pathFile = Paths.get(buildDirectoryPath.build(systemParents), systemNameFile);
        checkFile(pathFile);

        Files.delete(pathFile);

        return pathFile;
    }
}
