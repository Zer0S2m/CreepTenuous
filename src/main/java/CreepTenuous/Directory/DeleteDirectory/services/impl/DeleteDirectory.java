package CreepTenuous.Directory.DeleteDirectory.services.impl;

import CreepTenuous.Directory.BuilderDirectory.enums.Directory;
import CreepTenuous.Directory.DeleteDirectory.services.IDeleteDirectory;

import CreepTenuous.Directory.utils.check.CheckIsExistsDirectoryService;
import CreepTenuous.Directory.utils.build.BuildDirectoryPath;
import org.springframework.stereotype.Service;

import java.io.File;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;
import java.nio.file.Path;

@Service("delete-directory")
public class DeleteDirectory implements IDeleteDirectory, CheckIsExistsDirectoryService {
    public void delete(
            String[] parents,
            String name
    ) throws NoSuchFileException {
        Path path = Paths.get(BuildDirectoryPath.build(parents) + Directory.SEPARATOR.get() + name);

        checkDirectory(path);

        File removedDirectory = new File(path.toString());
        boolean isDeletedDirectory = removedDirectory.delete();
    }
}
