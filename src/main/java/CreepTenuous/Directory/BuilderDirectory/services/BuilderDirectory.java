package CreepTenuous.Directory.BuilderDirectory.services;

import CreepTenuous.Api.Directory.ManagerDirectory.data.DataMainPage;
import CreepTenuous.Directory.BuilderDirectory.enums.Directory;
import CreepTenuous.Api.enums.EDirectory;

import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.stereotype.Service;

@Service("builder-ready-directory")
public class BuilderDirectory implements IBuilderDirectory {
    private String[] arrPartsDirectory;
    private Integer level;

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
    public DataMainPage build(String[] arrPartsDirectory, Integer level) throws HttpMessageNotReadableException {
        this.arrPartsDirectory = arrPartsDirectory;
        this.level = level;

        if (level != arrPartsDirectory.length) {
            throw new HttpMessageNotReadableException(EDirectory.NOT_VALID_LEVEL.get());
        }

        return new DataMainPage(
                getArrPartsDirectory(),
                getDirectory(),
                this.level
        );
    }
}
