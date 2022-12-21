package CreepTenuous.services.directory.MoveDirectory.services.impl;

import CreepTenuous.services.directory.builderDirectory.enums.Directory;
import CreepTenuous.services.directory.MoveDirectory.services.IMoveDirectory;
import CreepTenuous.services.directory.utils.check.CheckIsExistsDirectoryService;
import CreepTenuous.services.directory.utils.build.BuildDirectoryPath;

import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.Arrays;

@Service("move-directory")
public class MoveDirectory implements IMoveDirectory, CheckIsExistsDirectoryService {
    public void move(
            String[] parents,
            String[] toParents,
            String nameDirectory
    ) throws IOException {
        Path currentPath = Paths.get(
                BuildDirectoryPath.build(parents) + Directory.SEPARATOR.get() + nameDirectory
        );
        Path newPath = Paths.get(BuildDirectoryPath.build(toParents));

        checkDirectory(currentPath);
        checkDirectory(newPath);

        ArrayList<String> createdNewArr = new ArrayList<String>(Arrays.asList(toParents));
        createdNewArr.add(nameDirectory);
        Path createdNewPath = Paths.get(
                BuildDirectoryPath.build(createdNewArr.toArray(new String[0]))
        );

        File newDirectory = createdNewPath.toFile();
        if (!newDirectory.exists()) {
            boolean isCreated = newDirectory.mkdir();
        }

        Files.move(currentPath, createdNewPath, StandardCopyOption.REPLACE_EXISTING);
    }
}
