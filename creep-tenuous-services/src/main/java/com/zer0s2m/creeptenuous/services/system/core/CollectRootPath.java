package com.zer0s2m.creeptenuous.services.system.core;

import java.nio.file.NoSuchFileException;

public interface CollectRootPath {
    String collect(String rawPath) throws NoSuchFileException;
}
