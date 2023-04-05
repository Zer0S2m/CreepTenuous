package CreepTenuous.services.files.delete.services.impl;

import CreepTenuous.services.directory.manager.enums.Directory;
import CreepTenuous.providers.build.os.services.impl.BuildDirectoryPath;
import CreepTenuous.services.files.delete.services.IDeleteFile;
import CreepTenuous.api.controllers.common.exceptions.NoSuchFileExistsException;
import CreepTenuous.providers.build.os.services.CheckIsExistsFileService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Service("delete-file")
public class DeleteFile implements IDeleteFile, CheckIsExistsFileService {
    private final BuildDirectoryPath buildDirectoryPath;

    @Autowired
    public DeleteFile(BuildDirectoryPath buildDirectoryPath) {
        this.buildDirectoryPath = buildDirectoryPath;
    }

    @Override
    public void delete(String nameFile, List<String> parents) throws IOException, NoSuchFileExistsException {
        Path pathFile = Paths.get(
                Paths.get(buildDirectoryPath.build(parents)) + Directory.SEPARATOR.get() + nameFile
        );
        checkFile(pathFile);

        Files.delete(pathFile);
    }
}
