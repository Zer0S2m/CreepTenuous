package com.zer0s2m.creeptenuous.services.system.impl;

import com.zer0s2m.creeptenuous.common.annotations.ServiceFileSystem;
import com.zer0s2m.creeptenuous.common.containers.ContainerDataBuilderDirectory;
import com.zer0s2m.creeptenuous.common.enums.Directory;
import com.zer0s2m.creeptenuous.common.exceptions.NotValidLevelDirectoryException;
import com.zer0s2m.creeptenuous.services.system.ServiceManagerDirectory;
import com.zer0s2m.creeptenuous.services.system.core.ServiceBuildDirectoryPath;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.nio.file.NoSuchFileException;
import java.util.List;

/**
 * A service for servicing all information and operations on file system objects. Use in end endpoints
 */
@ServiceFileSystem("service-manager-directory")
public class ServiceManagerDirectoryImpl implements ServiceManagerDirectory {

    private List<String> systemParents;
    
    private final ServiceCollectDirectoryImpl collectDirectory;

    private final ServiceBuilderDataFileSystemObjectImpl builderDataFile;

    private final ServiceBuildDirectoryPath buildDirectoryPath = new ServiceBuildDirectoryPath();

    @Autowired
    public ServiceManagerDirectoryImpl(ServiceCollectDirectoryImpl collectDirectory,
                                       ServiceBuilderDataFileSystemObjectImpl builderDataFile) {
        this.collectDirectory = collectDirectory;
        this.builderDataFile = builderDataFile;
    }

    /**
     * Build data in directory
     * @param systemParents parts of the system path - source
     * @param level level directory
     * @return data in directory
     * @throws IOException system error
     * @throws NotValidLevelDirectoryException invalid param directory level
     */
    @Override
    public ContainerDataBuilderDirectory build(@NotNull List<String> systemParents, Integer level)
            throws NotValidLevelDirectoryException, IOException {
        this.systemParents = systemParents;

        if (level != systemParents.toArray().length) {
            throw new NotValidLevelDirectoryException(Directory.NOT_VALID_LEVEL.get());
        }

        String directory = getDirectory();

        return new ContainerDataBuilderDirectory(
                getSystemParents(),
                level,
                builderDataFile.build(collectDirectory.collect(directory)).namesDirectory()
        );
    }

    /**
     * Set source path directory
     * @param systemParents parts of the system path - source
     */
    @Override
    public final void setSystemParents(List<String> systemParents) {
        this.systemParents = systemParents;
    }

    /**
     * Get directory
     * @return source path (string)
     * @throws NoSuchFileException not exists directory
     */
    @Override
    public final @NotNull String getDirectory() throws NoSuchFileException {
        return buildDirectoryPath.build(this.systemParents);
    }

    /**
     * Get source path directory
     * @return parts of the system path - source
     */
    @Override
    public final List<String> getSystemParents() {
        return this.systemParents;
    }

}
