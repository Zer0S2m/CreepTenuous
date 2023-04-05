package CreepTenuous.services.directory.copy.services.impl;

import CreepTenuous.services.directory.manager.enums.Directory;
import CreepTenuous.services.directory.copy.services.ICopyDirectory;
import CreepTenuous.providers.build.os.services.impl.BuildDirectoryPath;
import CreepTenuous.providers.build.os.services.CheckIsExistsDirectoryService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.stream.Stream;

@Service("copy-directory")
public class CopyDirectory implements ICopyDirectory, CheckIsExistsDirectoryService {
    private final BuildDirectoryPath buildDirectoryPath;

    @Autowired
    public CopyDirectory(BuildDirectoryPath buildDirectoryPath) {
        this.buildDirectoryPath = buildDirectoryPath;
    }

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
