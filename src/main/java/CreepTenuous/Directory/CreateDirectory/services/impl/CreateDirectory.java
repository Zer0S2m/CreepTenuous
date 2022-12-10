package CreepTenuous.Directory.CreateDirectory.services.impl;

import CreepTenuous.Directory.BuilderDirectory.enums.Directory;
import CreepTenuous.Directory.CreateDirectory.services.ICreateDirectory;

import CreepTenuous.Directory.utils.build.BuildDirectoryPath;
import org.springframework.stereotype.Service;

import java.io.File;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service("create-directory")
public class CreateDirectory implements ICreateDirectory {
    public void create(
            String[] parents,
            String name
    ) throws NoSuchFileException, FileAlreadyExistsException {
        Path path = Paths.get(BuildDirectoryPath.build(parents));
        Path pathNewDirectory = Paths.get(path + Directory.SEPARATOR.get() + name);

        checkDirectory(path, pathNewDirectory);

        File newDirectory = new File(pathNewDirectory.toString());
        boolean isCreated = newDirectory.mkdir();
    }
}
