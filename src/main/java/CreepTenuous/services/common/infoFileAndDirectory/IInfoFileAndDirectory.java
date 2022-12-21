package CreepTenuous.services.common.infoFileAndDirectory;

import CreepTenuous.api.controllers.common.infoFileAndDirectory.data.DataInfoAndDirectoryApi;

import java.nio.file.NoSuchFileException;

public interface IInfoFileAndDirectory {
    DataInfoAndDirectoryApi collect(String[] parents) throws NoSuchFileException;
}
