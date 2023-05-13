package com.zer0s2m.creeptenuous.services.system;

import com.zer0s2m.creeptenuous.common.containers.ContainerDataUploadFileSystemObject;
import com.zer0s2m.creeptenuous.common.containers.ContainerUploadFile;
import com.zer0s2m.creeptenuous.common.enums.Directory;
import com.zer0s2m.creeptenuous.services.system.utils.ThreadUnpackingDirectory;

import java.nio.file.Path;
import java.util.List;

public interface ServiceUnpackingDirectory {
    /**
     * Unpacking zip file to directory
     * @param container data zip files
     * @param outputDirectory target path
     */
    default List<ContainerDataUploadFileSystemObject> unpacking(ContainerUploadFile container, Path outputDirectory)
            throws InterruptedException {
        ThreadUnpackingDirectory threadUnpacking = new ThreadUnpackingDirectory(
                Directory.THREAD_NAME_UNPACKING_DIRECTORY.get(),
                container.getFiles(),
                outputDirectory
        );
        threadUnpacking.start();
        threadUnpacking.join();
        return threadUnpacking.getFinalData();
    }
}
