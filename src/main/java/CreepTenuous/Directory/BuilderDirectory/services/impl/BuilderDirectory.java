package CreepTenuous.Directory.BuilderDirectory.services.impl;

import CreepTenuous.Api.Directory.ManagerDirectory.data.DataManagerDirectory;
import CreepTenuous.Directory.BuilderDirectory.enums.Directory;
import CreepTenuous.Directory.BuilderDirectory.services.IBuilderDirectory;

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

    private String buildDirectory() {
        StringBuilder rawDirectory = new StringBuilder();
        for (String part : this.arrPartsDirectory) {
            rawDirectory.append(Directory.SEPARATOR.get()).append(part);
        }
        return rawDirectory.toString();
    }

    @Override
    public final String getDirectory() {
        return buildDirectory();
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
