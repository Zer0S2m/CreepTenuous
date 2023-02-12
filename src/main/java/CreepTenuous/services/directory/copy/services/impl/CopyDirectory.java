package CreepTenuous.services.directory.copy.services.impl;

import CreepTenuous.services.directory.builder.enums.Directory;
import CreepTenuous.services.directory.copy.services.ICopyDirectory;
import CreepTenuous.services.directory.utils.build.BuildDirectoryPath;
import CreepTenuous.services.directory.utils.check.CheckIsExistsDirectoryService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.stream.Stream;

@Service("copy-directory")
public class CopyDirectory implements ICopyDirectory, CheckIsExistsDirectoryService {
    @Autowired
    private BuildDirectoryPath buildDirectoryPath;

    @Override
    public void copy(
            List<String> parents,
            List<String> toParents,
            String nameDirectory
    ) throws IOException {
        Path currentPath = Paths.get(buildDirectoryPath.build(parents) + Directory.SEPARATOR.get(), nameDirectory);
        checkDirectory(currentPath);

        Path newPath = Paths.get(buildDirectoryPath.build(toParents));

        try (Stream<Path> stream = Files.walk(currentPath)) {
            stream.forEach(target -> {
                try {
                    Files.copy(
                            target,
                            newPath.resolve(currentPath.relativize(target)),
                            StandardCopyOption.REPLACE_EXISTING
                    );
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        }
    }
}