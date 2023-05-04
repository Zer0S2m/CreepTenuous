package com.zer0s2m.CreepTenuous.services.directory.upload.services;

import com.zer0s2m.CreepTenuous.services.core.Directory;
import com.zer0s2m.CreepTenuous.services.directory.upload.containers.ContainerDataUploadFile;
import com.zer0s2m.CreepTenuous.services.directory.upload.containers.ContainerUploadFile;
import com.zer0s2m.CreepTenuous.services.directory.upload.threads.ThreadUnpackingDirectory;

import java.nio.file.Path;
import java.util.List;

public interface IServiceUnpackingDirectory {
    /**
     * Unpacking zip file to directory
     * @param container data zip files
     * @param outputDirectory target path
     */
    default List<ContainerDataUploadFile> unpacking(ContainerUploadFile container, Path outputDirectory)
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
