package com.zer0s2m.creeptenuous.services.system.impl;

import com.zer0s2m.creeptenuous.common.annotations.ServiceFileSystem;
import com.zer0s2m.creeptenuous.common.enums.Directory;
import com.zer0s2m.creeptenuous.services.system.ServiceCollectDirectory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import java.util.function.Supplier;

@ServiceFileSystem("service-collect-directory")
public class ServiceCollectDirectoryImpl implements ServiceCollectDirectory {
    /**
     * Collect children in directory
     * @param path system source path
     * @return data: directories and files
     */
    @Override
    public ArrayList<List<Path>> collect(String path) {
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

        ArrayList<List<Path>> paths = new ArrayList<>();
        paths.add(stream.get().filter(Files::isRegularFile).toList());
        paths.add(stream.get().filter(Files::isDirectory).toList());
        return paths;
    }
}
