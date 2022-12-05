package CreepTenuous.Directory.BuilderDirectory.services;

import CreepTenuous.Api.Directory.ManagerDirectory.data.DataMainPage;

public interface IBuilderDirectory {
    String getDirectory();
    String[] getArrPartsDirectory();
    DataMainPage build(String[] arrPartsDirectory, Integer level);
}
