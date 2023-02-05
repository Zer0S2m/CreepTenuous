package CreepTenuous.services.files.create.service.impl;

import CreepTenuous.services.directory.utils.build.BuildDirectoryPath;

import CreepTenuous.services.files.create.exceptions.NotFoundTypeFileException;
import CreepTenuous.services.files.create.service.ICreateFile;

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
        conductor(path, nameFile, typeFile);
    }
}
