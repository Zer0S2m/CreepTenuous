package com.zer0s2m.creeptenuous.core.services;

import com.zer0s2m.creeptenuous.common.enums.Directory;
import org.jetbrains.annotations.NotNull;

import java.nio.file.NoSuchFileException;
import java.util.List;

/**
 * Service for servicing the paths of file system objects. Use in end endpoints
 */
public final class ServiceBuildDirectoryPath implements CheckIsExistsDirectoryService {

    private final CollectRootPath collectRootPath = new CollectRootPathImpl();

    /**
     * Get path
     * @param parents parts system folders
     * @return path
     * @throws NoSuchFileException system error
     */
    public @NotNull String build(@NotNull List<String> parents) throws NoSuchFileException {
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
    public @NotNull String build() throws NoSuchFileException {
        return collectRootPath.collect("");
    }

}
