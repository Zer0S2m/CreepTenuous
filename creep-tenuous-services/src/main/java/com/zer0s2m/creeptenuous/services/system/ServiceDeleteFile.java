package com.zer0s2m.creeptenuous.services.system;

import com.zer0s2m.creeptenuous.common.exceptions.NoSuchFileExistsException;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public interface ServiceDeleteFile {
    /**
     * Delete file from file system
     * @param systemNameFile system name file
     * @param systemParents system path part directories
     * @return source path system
     * @throws IOException error system
     * @throws NoSuchFileExistsException when no file in file system
     */
    Path delete(String systemNameFile, List<String> systemParents) throws IOException, NoSuchFileExistsException;
}
