package com.zer0s2m.creeptenuous.services.system;

import com.zer0s2m.creeptenuous.common.containers.ContainerDataUploadFileSystemObject;
import com.zer0s2m.creeptenuous.common.containers.ContainerUploadFile;
import com.zer0s2m.creeptenuous.common.enums.Directory;
import com.zer0s2m.creeptenuous.services.system.utils.ThreadUnpackingDirectory;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.util.List;

/**
 * Service to start unzipping a zip archive when uploading a directory (zip archive)
 */
public interface ServiceUnpackingDirectory {

    /**
     * Unpacking zip file to directory
     * @param container data zip files
     * @param outputDirectory target path
     * @param callClassName caller class from method
     */
    default List<ContainerDataUploadFileSystemObject> unpacking(
            @NotNull ContainerUploadFile container, Path outputDirectory, String callClassName)
            throws InterruptedException {
        ThreadUnpackingDirectory threadUnpacking = new ThreadUnpackingDirectory(
                Directory.THREAD_NAME_UNPACKING_DIRECTORY.get(),
                container.getFiles(),
                outputDirectory
        );
        threadUnpacking.setCallClassName(callClassName);
        threadUnpacking.start();
        threadUnpacking.join();
        return threadUnpacking.getFinalData();
    }

}
