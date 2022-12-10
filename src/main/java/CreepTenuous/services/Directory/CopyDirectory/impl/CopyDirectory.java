package CreepTenuous.services.Directory.CopyDirectory.impl;

import CreepTenuous.services.Directory.BuilderDirectory.enums.Directory;
import CreepTenuous.services.Directory.CopyDirectory.ICopyDirectory;
import CreepTenuous.services.Directory.utils.build.BuildDirectoryPath;
import CreepTenuous.services.Directory.utils.check.CheckIsExistsDirectoryService;

import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.*;
import java.util.stream.Stream;

@Service("copy-directory")
public class CopyDirectory implements ICopyDirectory, CheckIsExistsDirectoryService {
    @Override
    public void copy(
            String[] parents,
            String[] toParents,
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
