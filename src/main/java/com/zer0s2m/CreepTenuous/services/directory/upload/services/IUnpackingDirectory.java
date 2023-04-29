package com.zer0s2m.CreepTenuous.services.directory.upload.services;

import com.zer0s2m.CreepTenuous.services.core.Directory;
import com.zer0s2m.CreepTenuous.services.directory.upload.containers.ContainerUploadFile;
import com.zer0s2m.CreepTenuous.services.directory.upload.threads.ThreadUnpackingDirectory;

import java.nio.file.Path;

public interface IUnpackingDirectory {
    default void unpacking(ContainerUploadFile container, Path outputDirectory) {
        ThreadUnpackingDirectory threadUnpacking = new ThreadUnpackingDirectory(
                Directory.THREAD_NAME_UNPACKING_DIRECTORY.get(),
                container.getFiles(),
                outputDirectory
        );
        threadUnpacking.start();
    }
}
