package com.zer0s2m.CreepTenuous.providers.build.os.services.impl;

import com.zer0s2m.CreepTenuous.services.common.collectRootPath.impl.CollectRootPath;
import com.zer0s2m.CreepTenuous.services.core.Directory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.file.NoSuchFileException;
import java.util.List;

@Service("build-directory-path")
public class ServiceBuildDirectoryPath {
    private final CollectRootPath collectRootPath;

    @Autowired
    public ServiceBuildDirectoryPath(CollectRootPath collectRootPath) {
        this.collectRootPath = collectRootPath;
    }

    /**
     * Get path
     * @param parents parts system folders
     * @return path
     * @throws NoSuchFileException system error
     */
    public String build(List<String> parents) throws NoSuchFileException {
        StringBuilder rawDirectory = new StringBuilder();
        for (String part : parents) {
            rawDirectory.append(Directory.SEPARATOR.get()).append(part);
        }

        return collectRootPath.collect(rawDirectory.toString());
    }

    /**
     * Get root path
     * @return root path
     * @throws NoSuchFileException system error
     */
    public String build() throws NoSuchFileException {
        return collectRootPath.collect("");
    }
}
