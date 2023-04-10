package com.zer0s2m.CreepTenuous.services.common.collectRootPath;

import java.nio.file.NoSuchFileException;

public interface ICollectRootPath {
    String collect(String rawPath) throws NoSuchFileException;
}
