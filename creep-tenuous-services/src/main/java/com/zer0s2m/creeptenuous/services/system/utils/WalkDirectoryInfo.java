package com.zer0s2m.creeptenuous.services.system.utils;

import com.zer0s2m.creeptenuous.common.containers.ContainerInfoFileSystemObject;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/**
 * Directory scanning utilities.
 * <p>Required for Redis services {@link com.zer0s2m.creeptenuous.redis.services.system.base.BaseServiceFileSystemRedis} that copy file system objects</p>
 */
public interface WalkDirectoryInfo {

    /**
     * Get info directory from source path
     * <p><b>Replaces sources with target path</b></p>
     * @param source source system path
     * @param target target system path
     * @return info attached files and directories
     * @throws IOException error system
     */
    static @NotNull List<ContainerInfoFileSystemObject> walkDirectory(Path source, Path target) throws IOException {
        List<ContainerInfoFileSystemObject> attached = new ArrayList<>();
        try (Stream<Path> paths = Files.walk(source)) {
            paths
                    .forEach((attach) -> attached.add(new ContainerInfoFileSystemObject(
                            attach,
                            target != null ?
                                    Path.of(attach.toString().replace(source.toString(), target.toString())) : source,
                            attach.getFileName().toString(),
                            Files.isRegularFile(attach),
                            Files.isDirectory(attach)
                    )));
        }
        return attached;
    }

    /**
     * Get info directory from source path
     * <p>No replaces sources with target path</p>
     * @param source source system path
     * @return info attached files and directories
     * @throws IOException error system
     */
    static @NotNull List<ContainerInfoFileSystemObject> walkDirectory(Path source) throws IOException {
        return walkDirectory(source, null);
    }

}
