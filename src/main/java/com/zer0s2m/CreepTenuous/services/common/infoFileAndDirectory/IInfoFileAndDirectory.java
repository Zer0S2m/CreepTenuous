package com.zer0s2m.CreepTenuous.services.common.infoFileAndDirectory;

import com.zer0s2m.CreepTenuous.api.controllers.common.infoFileAndDirectory.data.DataInfoAndDirectoryApi;

import java.nio.file.NoSuchFileException;
import java.util.List;

public interface IInfoFileAndDirectory {
    DataInfoAndDirectoryApi collect(List<String> parents) throws NoSuchFileException;
}
