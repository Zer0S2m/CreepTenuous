package CreepTenuous.services.Directory.BuilderDirectory.services.impl;

import CreepTenuous.Api.controllers.Directory.ManagerDirectory.data.DataManagerDirectory;
import CreepTenuous.services.Directory.BuilderDirectory.enums.Directory;
import CreepTenuous.services.Directory.BuilderDirectory.services.IBuilderDirectory;
import CreepTenuous.services.Directory.utils.build.BuildDirectoryPath;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service("builder-ready-directory")
public class BuilderDirectory implements IBuilderDirectory {
    private String[] arrPartsDirectory;
    private Integer level;

    @Autowired
    private CollectDirectory collectDirectory;

    @Autowired
    private BuilderDataFile builderDataFile;

    @Override
    public final String getDirectory() {
        return BuildDirectoryPath.build(this.arrPartsDirectory);
    }

    @Override
    public final String[] getArrPartsDirectory() {
        return this.arrPartsDirectory;
    }

    @Override
    public DataManagerDirectory build(String[] arrPartsDirectory, Integer level)
            throws HttpMessageNotReadableException, IOException
    {
        this.arrPartsDirectory = arrPartsDirectory;
        this.level = level;

        if (level != arrPartsDirectory.length) {
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
