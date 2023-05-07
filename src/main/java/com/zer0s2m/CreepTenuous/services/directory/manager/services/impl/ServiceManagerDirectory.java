package com.zer0s2m.CreepTenuous.services.directory.manager.services.impl;

import com.zer0s2m.CreepTenuous.services.core.ServiceFileSystem;
import com.zer0s2m.CreepTenuous.services.core.Directory;
import com.zer0s2m.CreepTenuous.services.directory.manager.containers.ContainerDataBuilder;
import com.zer0s2m.CreepTenuous.services.directory.manager.exceptions.NotValidLevelDirectoryException;
import com.zer0s2m.CreepTenuous.services.directory.manager.services.IServiceManagerDirectory;
import com.zer0s2m.CreepTenuous.providers.build.os.services.impl.ServiceBuildDirectoryPath;

import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.nio.file.NoSuchFileException;
import java.util.List;

@ServiceFileSystem("builder-ready-directory")
public class ServiceManagerDirectory implements IServiceManagerDirectory {
    private List<String> systemParents;
    
    private final ServiceCollectDirectory collectDirectory;

    private final ServiceBuilderDataFile builderDataFile;

    private final ServiceBuildDirectoryPath buildDirectoryPath;

    @Autowired
    public ServiceManagerDirectory(
            ServiceCollectDirectory collectDirectory,
            ServiceBuilderDataFile builderDataFile,
            ServiceBuildDirectoryPath buildDirectoryPath
    ) {
        this.collectDirectory = collectDirectory;
        this.builderDataFile = builderDataFile;
        this.buildDirectoryPath = buildDirectoryPath;
    }

    /**
     * Build data in directory
     * @param systemParents parts of the system path - source
     * @param level level directory
     * @return data in directory
     * @throws IOException system error
     * @throws NotValidLevelDirectoryException invalid param directory level
     * @throws NoSuchFieldException not exists directory
     */
    @Override
    public ContainerDataBuilder build(List<String> systemParents, Integer level)
            throws NotValidLevelDirectoryException, IOException, NoSuchFieldException {
        this.systemParents = systemParents;

        if (level != systemParents.toArray().length) {
            throw new NotValidLevelDirectoryException(Directory.NOT_VALID_LEVEL.get());
        }

        String directory = getDirectory();

        return new ContainerDataBuilder(
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
    public final String getDirectory() throws NoSuchFileException {
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
