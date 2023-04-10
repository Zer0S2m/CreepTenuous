package com.zer0s2m.CreepTenuous.services.directory.upload.services;

import com.zer0s2m.CreepTenuous.services.directory.manager.enums.Directory;
import com.zer0s2m.CreepTenuous.services.directory.upload.containers.ContainerUploadFile;
import com.zer0s2m.CreepTenuous.services.directory.upload.threads.ThreadUnpackingDirectory;

import reactor.core.publisher.Mono;

import java.nio.file.Path;

public interface IUnpackingDirectory {
    default Mono<Void> unpacking(ContainerUploadFile container, Path outputDirectory) {
        return Mono.create(sink -> {
            ThreadUnpackingDirectory threadUnpacking = new ThreadUnpackingDirectory(
                    Directory.THREAD_NAME_UNPACKING_DIRECTORY.get(),
                    container.getFiles(),
                    outputDirectory
            );
            threadUnpacking.start();
            sink.success();
        });
    }
}
