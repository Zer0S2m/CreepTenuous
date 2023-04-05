package CreepTenuous.services.directory.manager.services;

import CreepTenuous.api.controllers.directory.manager.data.DataManagerDirectory;
import CreepTenuous.services.directory.manager.exceptions.NotValidLevelDirectoryException;

import java.io.IOException;
import java.nio.file.NoSuchFileException;
import java.util.List;

public interface IBuilderDirectory {
    String getDirectory() throws NoSuchFileException;

    List<String> getArrPartsDirectory();

    DataManagerDirectory build(List<String> arrPartsDirectory, Integer level)
            throws IOException, NotValidLevelDirectoryException;
}
