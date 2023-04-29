package com.zer0s2m.CreepTenuous.services.files.delete.services.impl;

import com.zer0s2m.CreepTenuous.services.core.Directory;
import com.zer0s2m.CreepTenuous.providers.build.os.services.impl.ServiceBuildDirectoryPath;
import com.zer0s2m.CreepTenuous.services.files.delete.services.IDeleteFile;
import com.zer0s2m.CreepTenuous.api.controllers.common.exceptions.NoSuchFileExistsException;
import com.zer0s2m.CreepTenuous.providers.build.os.services.CheckIsExistsFileService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Service("delete-file")
public class ServiceDeleteFile implements IDeleteFile, CheckIsExistsFileService {
    private final ServiceBuildDirectoryPath buildDirectoryPath;

    @Autowired
    public ServiceDeleteFile(ServiceBuildDirectoryPath buildDirectoryPath) {
        this.buildDirectoryPath = buildDirectoryPath;
    }

    @Override
    public void delete(String nameFile, List<String> parents) throws IOException, NoSuchFileExistsException {
        Path pathFile = Paths.get(
                Paths.get(buildDirectoryPath.build(parents)) + Directory.SEPARATOR.get() + nameFile
        );
        checkFile(pathFile);

        Files.delete(pathFile);
    }
}
