package com.zer0s2m.CreepTenuous.services.directory.move.services;

import com.zer0s2m.CreepTenuous.services.directory.move.containers.ContainerInfoFileSystemObject;
import com.zer0s2m.CreepTenuous.services.directory.move.containers.ContainerMoveDirectory;
import com.zer0s2m.CreepTenuous.services.directory.move.enums.MethodMoveDirectory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.nio.file.Path;
import java.nio.file.Files;
import java.util.stream.Stream;

public interface IServiceMoveDirectory {
    /**
     * Move directory
     * @param systemParents system path part directories
     * @param systemToParents system path part directories
     * @param systemNameDirectory system name directory
     * @param method method moving {@link MethodMoveDirectory}
     * @return info target and source path
     * @throws IOException error system
     */
    ContainerMoveDirectory move(
            List<String> systemParents,
            List<String> systemToParents,
            String systemNameDirectory,
            Integer method
    ) throws IOException;

    /**
     * Get info directory from source path
     * @param source source system path
     * @param target target system path
     * @return info attached files and directories
     * @throws IOException error system
     */
    default List<ContainerInfoFileSystemObject> walkDirectory(Path source, Path target) throws IOException {
        List<ContainerInfoFileSystemObject> attached = new ArrayList<>();
        try (Stream<Path> paths = Files.walk(source)) {
            paths
                    .forEach((attach) -> attached.add(new ContainerInfoFileSystemObject(
                            attach,
                            Path.of(attach.toString().replace(source.toString(), target.toString())),
                            attach.getFileName().toString(),
                            Files.isRegularFile(attach),
                            Files.isDirectory(attach)
                    )));
        }
        return attached;
    }
}
