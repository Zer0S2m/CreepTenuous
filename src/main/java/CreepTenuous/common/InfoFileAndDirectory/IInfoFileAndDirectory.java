package CreepTenuous.common.InfoFileAndDirectory;

import CreepTenuous.Api.common.InfoFileAndDirectory.data.DataInfoAndDirectoryApi;

public interface IInfoFileAndDirectory {
    DataInfoAndDirectoryApi collect(String[] parents);
}
