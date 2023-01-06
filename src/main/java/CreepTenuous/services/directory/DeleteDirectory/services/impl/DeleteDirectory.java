package CreepTenuous.services.directory.DeleteDirectory.services.impl;

import CreepTenuous.services.directory.builderDirectory.enums.Directory;
import CreepTenuous.services.directory.DeleteDirectory.services.IDeleteDirectory;
import CreepTenuous.services.directory.utils.build.BuildDirectoryPath;
import CreepTenuous.services.directory.utils.check.CheckIsExistsDirectoryService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;
import java.nio.file.Path;
import java.util.List;

@Service("delete-directory")
public class DeleteDirectory implements IDeleteDirectory, CheckIsExistsDirectoryService {
    @Autowired
    private BuildDirectoryPath buildDirectoryPath;

    @Override
    public void delete(
            List<String> parents,
            String name
    ) throws NoSuchFileException {
        Path path = Paths.get(buildDirectoryPath.build(parents) + Directory.SEPARATOR.get() + name);
        checkDirectory(path);

        File removedDirectory = path.toFile();
        boolean isDeletedDirectory = removedDirectory.delete();
    }
}
