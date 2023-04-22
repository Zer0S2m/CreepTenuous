package com.zer0s2m.CreepTenuous.services.directory.manager.services;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public interface ICollectDirectory {
    List<List<Path>> collect(String path) throws IOException;
}
