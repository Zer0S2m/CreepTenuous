package CreepTenuous.services.files.create.services.impl;

import CreepTenuous.providers.build.os.services.impl.BuildDirectoryPath;
import CreepTenuous.services.files.create.exceptions.NotFoundTypeFileException;
import CreepTenuous.services.files.create.services.ICreateFile;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;

@Service("create-file")
public class CreateFile implements ICreateFile {
    private final BuildDirectoryPath buildDirectoryPath;

    @Autowired
    public CreateFile(BuildDirectoryPath buildDirectoryPath) {
        this.buildDirectoryPath = buildDirectoryPath;
    }

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
