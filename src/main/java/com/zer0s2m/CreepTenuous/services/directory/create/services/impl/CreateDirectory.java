package com.zer0s2m.CreepTenuous.services.directory.create.services.impl;

import com.zer0s2m.CreepTenuous.services.directory.manager.enums.Directory;
import com.zer0s2m.CreepTenuous.services.directory.create.services.ICreateDirectory;
import com.zer0s2m.CreepTenuous.providers.build.os.services.impl.ServiceBuildDirectoryPath;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Service("create-directory")
public class CreateDirectory implements ICreateDirectory {
    private final ServiceBuildDirectoryPath buildDirectoryPath;

    @Autowired
    public CreateDirectory(ServiceBuildDirectoryPath buildDirectoryPath) {
        this.buildDirectoryPath = buildDirectoryPath;
    }

    @Override
    public void create(
            List<String> parents,
            String name
    ) throws NoSuchFileException, FileAlreadyExistsException {
        Path path = Paths.get(buildDirectoryPath.build(parents));
        Path pathNewDirectory = Paths.get(path + Directory.SEPARATOR.get() + name);

        checkDirectory(pathNewDirectory);

        File newDirectory = new File(pathNewDirectory.toString());
        newDirectory.mkdir();
    }
}
