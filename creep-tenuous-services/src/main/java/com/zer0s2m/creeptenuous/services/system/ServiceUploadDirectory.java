package com.zer0s2m.creeptenuous.services.system;

import com.zer0s2m.creeptenuous.common.http.ResponseUploadDirectoryApi;

import java.io.IOException;
import java.nio.file.Path;

/**
 * Service for servicing directory uploading
 */
public interface ServiceUploadDirectory {

    /**
     * Run thread for unpacking zip archive
     * @param systemPath system path from part directories
     * @param source path zip file in file system
     * @return data upload
     * @throws IOException system error
     */
    ResponseUploadDirectoryApi upload(Path systemPath, Path source) throws IOException;

}
