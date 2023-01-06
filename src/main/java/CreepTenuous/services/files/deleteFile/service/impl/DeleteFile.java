package CreepTenuous.services.files.deleteFile.service.impl;

import CreepTenuous.services.directory.builderDirectory.enums.Directory;
import CreepTenuous.services.directory.utils.build.BuildDirectoryPath;
import CreepTenuous.services.files.deleteFile.service.IDeleteFile;
import CreepTenuous.services.files.enums.ExceptionFile;
import CreepTenuous.services.files.deleteFile.exceptions.NoSuchFileExistsException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Service("delete-file")
public class DeleteFile implements IDeleteFile {
    @Autowired
    private BuildDirectoryPath buildDirectoryPath;

    @Override
    public void delete(String nameFile, List<String> parents) throws IOException, NoSuchFileExistsException {
        Path path = Paths.get(buildDirectoryPath.build(parents));
        if (!Files.exists(path)) {
            throw new NoSuchFileException(Directory.NOT_FOUND_DIRECTORY.get());
        }

        Path pathFile = Paths.get(path.toString() + Directory.SEPARATOR.get() + nameFile);
        if (!Files.exists(pathFile)) {
            throw new NoSuchFileExistsException(ExceptionFile.FILE_NOT_EXISTS.get());
        }

        Files.delete(pathFile);
    }
}
