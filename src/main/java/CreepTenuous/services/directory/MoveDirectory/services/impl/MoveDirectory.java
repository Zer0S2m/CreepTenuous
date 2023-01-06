package CreepTenuous.services.directory.MoveDirectory.services.impl;

import CreepTenuous.services.directory.builderDirectory.enums.Directory;
import CreepTenuous.services.directory.MoveDirectory.services.IMoveDirectory;
import CreepTenuous.services.directory.utils.build.BuildDirectoryPath;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.List;

@Service("move-directory")
public class MoveDirectory implements IMoveDirectory {
    @Autowired
    private BuildDirectoryPath buildDirectoryPath;

    @Override
    public void move(
            List<String> parents,
            List<String> toParents,
            String nameDirectory
    ) throws IOException {
        Path currentPath = Paths.get(
                buildDirectoryPath.build(parents) + Directory.SEPARATOR.get() + nameDirectory
        );
        Path newPath = Paths.get(buildDirectoryPath.build(toParents));

        toParents.add(nameDirectory);
        Path createdNewPath = Paths.get(
                buildDirectoryPath.build(toParents)
        );

        File newDirectory = createdNewPath.toFile();
        if (!newDirectory.exists()) {
            boolean isCreated = newDirectory.mkdir();
        }

        Files.move(currentPath, createdNewPath, StandardCopyOption.REPLACE_EXISTING);
    }
}
