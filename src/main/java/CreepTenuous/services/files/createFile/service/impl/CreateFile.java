package CreepTenuous.services.files.createFile.service.impl;

import CreepTenuous.services.directory.builderDirectory.enums.Directory;
import CreepTenuous.services.directory.utils.build.BuildDirectoryPath;

import CreepTenuous.services.files.createFile.exceptions.NotFoundTypeFileException;
import CreepTenuous.services.files.createFile.service.ICreateFile;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;

@Service("create-file")
public class CreateFile implements ICreateFile {
    @Autowired
    private BuildDirectoryPath buildDirectoryPath;

    @Override
    public void create(
            List<String> parents,
            String nameFile,
            Integer typeFile
    ) throws NotFoundTypeFileException, IOException {
        checkTypeFile(typeFile);

        Path path = Paths.get(buildDirectoryPath.build(parents));
        if (!Files.exists(path)) {
            throw new NoSuchFileException(Directory.NOT_FOUND_DIRECTORY.get());
        }

        conductor(path, nameFile, typeFile);
    }
}
