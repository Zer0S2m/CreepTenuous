package CreepTenuous.Directory.BuilderDirectory.services.impl;

import CreepTenuous.Api.enums.EDirectory;
import CreepTenuous.Directory.BuilderDirectory.services.ICollectDirectory;

import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Stream;

@Service("collect-directory")
public class CollectDirectory implements ICollectDirectory {
    @Override
    public List<Path> collect(String path) throws IOException {
        try (Stream<Path> stream = Files.walk(Paths.get("/" + path), 1)) {
            return stream.toList();
        } catch (NoSuchFileException error) {
            throw new NoSuchFileException(EDirectory.NOT_FOUND_DIRECTORY.get());
        }
    }
}
