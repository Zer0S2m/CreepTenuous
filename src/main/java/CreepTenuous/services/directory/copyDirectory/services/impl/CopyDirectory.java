package CreepTenuous.services.directory.copyDirectory.services.impl;

import CreepTenuous.services.directory.builderDirectory.enums.Directory;
import CreepTenuous.services.directory.copyDirectory.services.ICopyDirectory;
import CreepTenuous.services.directory.utils.build.BuildDirectoryPath;
import CreepTenuous.services.directory.utils.check.CheckIsExistsDirectoryService;

import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.stream.Stream;

@Service("copy-directory")
public class CopyDirectory implements ICopyDirectory, CheckIsExistsDirectoryService {
    @Override
    public void copy(
            List<String> parents,
            List<String> toParents,
            String nameDirectory
    ) throws IOException {
        Path currentPath = Paths.get(BuildDirectoryPath.build(parents) + Directory.SEPARATOR.get(), nameDirectory);
        Path newPath = Paths.get(BuildDirectoryPath.build(toParents));

        checkDirectory(currentPath);
        checkDirectory(newPath);

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
