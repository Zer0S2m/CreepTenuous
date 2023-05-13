package com.zer0s2m.creeptenuous.services.system.impl;

import com.zer0s2m.creeptenuous.common.annotations.ServiceFileSystem;
import com.zer0s2m.creeptenuous.common.containers.ContainerDataCreateDirectory;
import com.zer0s2m.creeptenuous.services.system.core.Distribution;
import com.zer0s2m.creeptenuous.services.system.core.ServiceBuildDirectoryPath;
import com.zer0s2m.creeptenuous.services.system.ServiceCreateDirectory;
import org.springframework.beans.factory.annotation.Autowired;

import java.nio.file.FileAlreadyExistsException;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@ServiceFileSystem("service-create-directory")
public class ServiceCreateDirectoryImpl implements ServiceCreateDirectory {
    private final ServiceBuildDirectoryPath buildDirectoryPath;

    @Autowired
    public ServiceCreateDirectoryImpl(ServiceBuildDirectoryPath buildDirectoryPath) {
        this.buildDirectoryPath = buildDirectoryPath;
    }

    /**
     * Create directory in system
     * @param systemParents parts of the system path - source
     * @param nameDirectory real system name
     * @return data create directory
     * @throws NoSuchFileException no directory in system
     * @throws FileAlreadyExistsException directory exists
     */
    @Override
    public ContainerDataCreateDirectory create(
            List<String> systemParents,
            String nameDirectory
    ) throws NoSuchFileException, FileAlreadyExistsException {
        Path path = Paths.get(buildDirectoryPath.build(systemParents));

        String newNameDirectory = Distribution.getUUID();
        Path pathNewDirectory = Path.of(path.toString(), newNameDirectory);

        checkDirectory(pathNewDirectory);

        pathNewDirectory.toFile().mkdir();

        return new ContainerDataCreateDirectory(nameDirectory, newNameDirectory, pathNewDirectory);
    }
}
