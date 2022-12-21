package CreepTenuous.services.directory.builderDirectory.services.impl;

import CreepTenuous.services.directory.builderDirectory.enums.Directory;
import CreepTenuous.services.directory.builderDirectory.services.ICollectDirectory;

import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import java.util.function.Supplier;

@Service("collect-directory")
public class CollectDirectory implements ICollectDirectory {
    @Override
    public ArrayList<List<Path>> collect(String path) throws IOException {
        Path _path = Paths.get("/" + path);
        Supplier<Stream<Path>> stream = () -> {
            try {
                return Files.walk(_path, 1);
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
