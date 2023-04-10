package com.zer0s2m.CreepTenuous.services.files.copy.services;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public interface ICopyFile {
    void copy(String nameFile, List<String> parents, List<String> toParents) throws IOException;

    void copy(List<String> nameFiles, List<String> parents, List<String> toParents) throws IOException;

    void copy(Path source, Path target) throws IOException;
}
