package com.zer0s2m.CreepTenuous.services.directory.upload.containers;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class ContainerUploadFile {
    private final List<Path> files;

    public ContainerUploadFile() {
        this.files = new ArrayList<>();
    }

    public void setFile(Path file) {
        files.add(file);
    }

    public List<Path> getFiles() {
        return files;
    }
}
