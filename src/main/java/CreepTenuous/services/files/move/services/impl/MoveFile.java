package CreepTenuous.services.files.move.services.impl;

import CreepTenuous.providers.build.os.services.impl.BuildDirectoryPath;
import CreepTenuous.services.directory.manager.enums.Directory;
import CreepTenuous.services.files.move.services.IMoveFile;

import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;

import static java.nio.file.StandardCopyOption.ATOMIC_MOVE;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

@Service("move-file")
public class MoveFile implements IMoveFile {
    private final BuildDirectoryPath buildDirectoryPath;

    public MoveFile(BuildDirectoryPath buildDirectoryPath) {
        this.buildDirectoryPath = buildDirectoryPath;
    }

    public void move(String nameFile, List<String> parents, List<String> toParents) throws IOException {
        Path currentPath = Paths.get(
                Paths.get(buildDirectoryPath.build(parents)) + Directory.SEPARATOR.get() + nameFile
        );
        Path createdNewPath = Paths.get(
                Paths.get(buildDirectoryPath.build(toParents)) + Directory.SEPARATOR.get() + nameFile
        );

        move(currentPath, createdNewPath);
    }

    public void move(Path source, Path target) throws IOException {
        Files.move(source, target, ATOMIC_MOVE, REPLACE_EXISTING);
    }
}
