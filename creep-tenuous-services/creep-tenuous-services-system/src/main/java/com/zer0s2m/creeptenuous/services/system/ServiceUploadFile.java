package com.zer0s2m.creeptenuous.services.system;

import com.zer0s2m.creeptenuous.common.containers.ContainerDataUploadFileFragment;
import com.zer0s2m.creeptenuous.common.http.ResponseObjectUploadFileApi;
import com.zer0s2m.creeptenuous.core.atomic.AtomicServiceFileSystem;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

/**
 * An interface for implementing a class that works with downloading files,
 * as well as their fragmentation and operation in atomic mode {@link AtomicServiceFileSystem}.
 */
public interface ServiceUploadFile extends AtomicServiceFileSystem {

    /**
     * Upload files.
     * @param files Files.
     * @param systemParents System names of directories from which the directory path will be collected
     *                      where the file will be downloaded.
     * @return Info is upload and info system file object.
     * @throws IOException If an I/O error occurs or the parent directory does not exist.
     */
    List<ResponseObjectUploadFileApi> upload(Map<Path, String> files, List<String> systemParents)
            throws IOException;

    /**
     * Upload files and fragment them into parts.
     * @param inputStreams File streams.
     * @param systemParents System names of directories from which the directory path will be collected
     *                      where the file will be downloaded.
     * @param originFileNames Original file names.
     * @return Execution result.
     * @throws IOException If an I/O error occurs or the parent directory does not exist.
     */
    List<ContainerDataUploadFileFragment> uploadFragment(
            List<InputStream> inputStreams, List<String> originFileNames, List<String> systemParents)
            throws IOException;

}
