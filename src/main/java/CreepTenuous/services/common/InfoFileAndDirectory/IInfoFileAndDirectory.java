package CreepTenuous.services.common.InfoFileAndDirectory;

import CreepTenuous.Api.controllers.common.InfoFileAndDirectory.data.DataInfoAndDirectoryApi;

import java.nio.file.NoSuchFileException;

public interface IInfoFileAndDirectory {
    DataInfoAndDirectoryApi collect(String[] parents) throws NoSuchFileException;
}
