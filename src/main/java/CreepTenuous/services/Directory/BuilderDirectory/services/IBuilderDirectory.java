package CreepTenuous.services.Directory.BuilderDirectory.services;

import CreepTenuous.Api.controllers.Directory.ManagerDirectory.data.DataManagerDirectory;
import org.springframework.http.converter.HttpMessageNotReadableException;

import java.io.IOException;

public interface IBuilderDirectory {
    String getDirectory();
    String[] getArrPartsDirectory();
    DataManagerDirectory build(String[] arrPartsDirectory, Integer level)
            throws IOException, HttpMessageNotReadableException;
}
