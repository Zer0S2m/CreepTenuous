package com.zer0s2m.creeptenuous.core.services.impl;

import com.zer0s2m.creeptenuous.common.annotations.ServiceFileSystem;
import com.zer0s2m.creeptenuous.common.enums.Directory;
import com.zer0s2m.creeptenuous.core.services.ServiceCollectDirectory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import java.util.function.Supplier;

/**
 * Service for collecting information about the directory and its objects
 */
@ServiceFileSystem("service-collect-directory")
public class ServiceCollectDirectoryImpl implements ServiceCollectDirectory {

    /**
     * Collect children in directory
     * @param path system source path
     * @return data: directories and files
     */
    @Override
    public List<List<Path>> collect(String path) {
        Path source = Paths.get(path);
        Supplier<Stream<Path>> stream = () -> {
            try {
                return Files.walk(source, 1);
            } catch (IOException e) {
                try {
                    throw new NoSuchFileException(Directory.NOT_FOUND_DIRECTORY.get());
                } catch (NoSuchFileException ex) {
                    throw new RuntimeException(ex);
                }
            }
        };

        List<List<Path>> paths = new ArrayList<>();
        paths.add(stream.get().filter(Files::isRegularFile).toList());
        paths.add(stream.get().filter(Files::isDirectory).toList());
        paths = paths
                .stream()
                .map(path1 -> path1
                        .stream()
                        .filter(path2 -> !path2.getFileName().toString().equals(source.getFileName().toString()))
                        .toList())
                .toList();
        return paths;
    }

}
