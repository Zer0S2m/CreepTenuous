package CreepTenuous.services.directory.createDirectory.services.impl;

import CreepTenuous.services.directory.builderDirectory.enums.Directory;
import CreepTenuous.services.directory.createDirectory.services.ICreateDirectory;
import CreepTenuous.services.directory.utils.build.BuildDirectoryPath;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Service("create-directory")
public class CreateDirectory implements ICreateDirectory {
    @Autowired
    private BuildDirectoryPath buildDirectoryPath;

    @Override
    public void create(
            List<String> parents,
            String name
    ) throws NoSuchFileException, FileAlreadyExistsException {
        Path path = Paths.get(buildDirectoryPath.build(parents));
        Path pathNewDirectory = Paths.get(path + Directory.SEPARATOR.get() + name);

        checkDirectory(pathNewDirectory);

        File newDirectory = new File(pathNewDirectory.toString());
        boolean isCreated = newDirectory.mkdir();
    }
}
