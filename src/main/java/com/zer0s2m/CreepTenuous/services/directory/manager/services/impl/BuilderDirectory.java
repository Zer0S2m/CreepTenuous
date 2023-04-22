package com.zer0s2m.CreepTenuous.services.directory.manager.services.impl;

import com.zer0s2m.CreepTenuous.api.controllers.directory.manager.data.DataManagerDirectory;
import com.zer0s2m.CreepTenuous.services.directory.manager.enums.Directory;
import com.zer0s2m.CreepTenuous.services.directory.manager.exceptions.NotValidLevelDirectoryException;
import com.zer0s2m.CreepTenuous.services.directory.manager.services.IBuilderDirectory;
import com.zer0s2m.CreepTenuous.providers.build.os.services.impl.ServiceBuildDirectoryPath;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.NoSuchFileException;
import java.util.List;

@Service("builder-ready-directory")
public class BuilderDirectory implements IBuilderDirectory {
    private List<String> arrPartsDirectory;
    
    private final CollectDirectory collectDirectory;

    private final BuilderDataFile builderDataFile;

    private final ServiceBuildDirectoryPath buildDirectoryPath;

    @Autowired
    public BuilderDirectory(
            CollectDirectory collectDirectory, 
            BuilderDataFile builderDataFile, 
            ServiceBuildDirectoryPath buildDirectoryPath
    ) {
        this.collectDirectory = collectDirectory;
        this.builderDataFile = builderDataFile;
        this.buildDirectoryPath = buildDirectoryPath;
    }

    @Override
    public final String getDirectory() throws NoSuchFileException {
        return buildDirectoryPath.build(this.arrPartsDirectory);
    }

    @Override
    public final List<String> getArrPartsDirectory() {
        return this.arrPartsDirectory;
    }

    @Override
    public DataManagerDirectory build(List<String> arrPartsDirectory, Integer level)
            throws NotValidLevelDirectoryException, IOException
    {
        this.arrPartsDirectory = arrPartsDirectory;

        if (level != arrPartsDirectory.toArray().length) {
            throw new NotValidLevelDirectoryException(Directory.NOT_VALID_LEVEL.get());
        }

        String directory = getDirectory();
        List<Object> paths = builderDataFile.build(collectDirectory.collect(directory));

        return new DataManagerDirectory(
                getArrPartsDirectory(),
                directory,
                level,
                paths
        );
    }
}
