package CreepTenuous.services.directory.upload.containers;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class ContainerUploadFile {
    private List<Path> files;

    public ContainerUploadFile() {
        this.files = new ArrayList<>();
    }

    public Path setFile(Path file) {
        files.add(file);
        return file;
    }

    public List<Path> getFiles() {
        return files;
    }
}