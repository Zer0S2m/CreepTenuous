package com.zer0s2m.creeptenuous.services.system.core;

import com.zer0s2m.creeptenuous.common.components.RootPath;
import com.zer0s2m.creeptenuous.common.enums.Directory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;
import java.util.Objects;

@Service("collect-root-path")
public final class CollectRootPathImpl implements CollectRootPath, CheckIsExistsDirectoryService {
    private final RootPath rootPath;

    @Autowired
    public CollectRootPathImpl(RootPath rootPath) {
        this.rootPath = rootPath;
    }

    @Override
    public String collect(String rawPath) throws NoSuchFileException {
        String path = this.rootPath.getRootPath();

        if (rawPath.length() != 0 && Objects.equals(rawPath.charAt(0), '/')) {
            path = path + Directory.SEPARATOR.get() + rawPath;
        } else {
            path = path + rawPath;
        }

        checkDirectory(Paths.get(path));

        return path;
    }
}
