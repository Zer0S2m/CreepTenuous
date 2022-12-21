package CreepTenuous.services.directory.builderDirectory.services;

import CreepTenuous.api.controllers.directory.managerDirectory.data.DataManagerDirectory;
import org.springframework.http.converter.HttpMessageNotReadableException;

import java.io.IOException;

public interface IBuilderDirectory {
    String getDirectory();
    String[] getArrPartsDirectory();
    DataManagerDirectory build(String[] arrPartsDirectory, Integer level)
            throws IOException, HttpMessageNotReadableException;
}
