package CreepTenuous.services.files.delete.service.impl;

import CreepTenuous.services.directory.builder.enums.Directory;
import CreepTenuous.services.directory.utils.build.BuildDirectoryPath;
import CreepTenuous.services.files.delete.service.IDeleteFile;
import CreepTenuous.api.controllers.common.exceptions.NoSuchFileExistsException;
import CreepTenuous.services.files.utils.check.CheckIsExistsFileService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Service("delete-file")
public class DeleteFile implements IDeleteFile, CheckIsExistsFileService {
    @Autowired
    private BuildDirectoryPath buildDirectoryPath;

    @Override
    public void delete(String nameFile, List<String> parents) throws IOException, NoSuchFileExistsException {
        Path pathFile = Paths.get(
                Paths.get(buildDirectoryPath.build(parents)) + Directory.SEPARATOR.get() + nameFile
        );
        checkFile(pathFile);

        Files.delete(pathFile);
    }
}
