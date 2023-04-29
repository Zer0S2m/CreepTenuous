package com.zer0s2m.CreepTenuous.services.directory.create.services.impl;

import com.zer0s2m.CreepTenuous.providers.build.os.core.Distribution;
import com.zer0s2m.CreepTenuous.services.core.ServiceFileSystem;
import com.zer0s2m.CreepTenuous.services.directory.create.containers.ContainerDataCreatedDirectory;
import com.zer0s2m.CreepTenuous.services.directory.create.services.ICreateDirectory;
import com.zer0s2m.CreepTenuous.providers.build.os.services.impl.ServiceBuildDirectoryPath;

import org.springframework.beans.factory.annotation.Autowired;

import java.nio.file.FileAlreadyExistsException;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@ServiceFileSystem("create-directory")
public class ServiceCreateDirectory implements ICreateDirectory {
    private final ServiceBuildDirectoryPath buildDirectoryPath;

    @Autowired
    public ServiceCreateDirectory(ServiceBuildDirectoryPath buildDirectoryPath) {
        this.buildDirectoryPath = buildDirectoryPath;
    }

    @Override
    public ContainerDataCreatedDirectory create(
            List<String> systemParents,
            String nameDirectory
    ) throws NoSuchFileException, FileAlreadyExistsException {
        Path path = Paths.get(buildDirectoryPath.build(systemParents));

        String newNameDirectory = Distribution.getUUID();
        Path pathNewDirectory = Path.of(path.toString(), newNameDirectory);

        checkDirectory(pathNewDirectory);

        pathNewDirectory.toFile().mkdir();

        return new ContainerDataCreatedDirectory(nameDirectory, newNameDirectory, pathNewDirectory);
    }
}
