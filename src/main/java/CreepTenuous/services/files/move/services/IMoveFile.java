package CreepTenuous.services.files.move.services;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public interface IMoveFile {
    void move(String nameFile, List<String> parents, List<String> toParents) throws IOException;

    void move(List<String> nameFiles, List<String> parents, List<String> toParents) throws IOException;

    void move(Path source, Path target) throws IOException;
}
