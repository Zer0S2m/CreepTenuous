package CreepTenuous.services.directory.builderDirectory.services;

import CreepTenuous.api.controllers.directory.managerDirectory.data.DataManagerDirectory;
import org.springframework.http.converter.HttpMessageNotReadableException;

import java.io.IOException;
import java.nio.file.NoSuchFileException;
import java.util.List;

public interface IBuilderDirectory {
    String getDirectory() throws NoSuchFileException;
    List<String> getArrPartsDirectory();
    DataManagerDirectory build(List<String> arrPartsDirectory, Integer level)
            throws IOException, HttpMessageNotReadableException;
}
