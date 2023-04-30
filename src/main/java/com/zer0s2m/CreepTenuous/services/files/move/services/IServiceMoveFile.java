package com.zer0s2m.CreepTenuous.services.files.move.services;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public interface IServiceMoveFile {
    Path move(String systemNameFile, List<String> systemParents, List<String> systemToParents) throws IOException;

    Path move(List<String> systemNameFiles, List<String> systemParents, List<String> systemToParents)
            throws IOException;

    Path move(Path source, Path target) throws IOException;
}
