package com.zer0s2m.creeptenuous.common.containers;

import lombok.Getter;
import lombok.Setter;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

@Getter
public class ContainerUploadFile {

    private final List<Path> files;

    public ContainerUploadFile() {
        this.files = new ArrayList<>();
    }

    public void setFile(Path file) {
        files.add(file);
    }

}
