package com.zer0s2m.creeptenuous.services.system;

import com.zer0s2m.creeptenuous.common.http.ResponseUploadDirectoryApi;
import com.zer0s2m.creeptenuous.core.atomic.AtomicServiceFileSystem;

import java.io.IOException;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.List;

/**
 * Service for servicing directory uploading
 */
public interface ServiceUploadDirectory extends ServiceUnpackingDirectory, AtomicServiceFileSystem {

    /**
     * Run thread for unpacking zip archive
     * @param systemPath system path from part directories
     * @param source path zip file in file system
     * @return data upload
     * @throws IOException system error
     */
    ResponseUploadDirectoryApi upload(Path systemPath, Path source) throws IOException;

    /**
     * Get system path from part directories
     * @param systemParents system path part directories
     * @return system path
     * @throws NoSuchFileException no file object in file system
     */
    Path getPath(List<String> systemParents) throws NoSuchFileException;

}
