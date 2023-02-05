package CreepTenuous.services.directory.builder.services.impl;

import CreepTenuous.api.controllers.directory.manager.data.DataManagerDirectory;
import CreepTenuous.services.directory.builder.enums.Directory;
import CreepTenuous.services.directory.builder.services.IBuilderDirectory;
import CreepTenuous.services.directory.utils.build.BuildDirectoryPath;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.NoSuchFileException;
import java.util.List;

@Service("builder-ready-directory")
public class BuilderDirectory implements IBuilderDirectory {
    private List<String> arrPartsDirectory;
    private Integer level;

    @Autowired
    private CollectDirectory collectDirectory;

    @Autowired
    private BuilderDataFile builderDataFile;

    @Autowired
    private BuildDirectoryPath buildDirectoryPath;

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
            throws HttpMessageNotReadableException, IOException
    {
        this.arrPartsDirectory = arrPartsDirectory;
        this.level = level;

        if (level != arrPartsDirectory.toArray().length) {
            throw new HttpMessageNotReadableException(Directory.NOT_VALID_LEVEL.get());
        }

        String directory = getDirectory();
        List<Object> paths = builderDataFile.build(collectDirectory.collect(directory));

        return new DataManagerDirectory(
                getArrPartsDirectory(),
                directory,
                this.level,
                paths
        );
    }
}
