package com.zer0s2m.CreepTenuous.services.directory.upload.services.impl;

import com.zer0s2m.CreepTenuous.api.controllers.directory.upload.http.ResponseUploadDirectory;
import com.zer0s2m.CreepTenuous.services.directory.manager.enums.Directory;
import com.zer0s2m.CreepTenuous.services.directory.upload.containers.ContainerUploadFile;
import com.zer0s2m.CreepTenuous.services.directory.upload.services.IUploadDirectory;
import com.zer0s2m.CreepTenuous.services.directory.upload.services.IUnpackingDirectory;
import com.zer0s2m.CreepTenuous.providers.build.os.services.impl.ServiceBuildDirectoryPath;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.http.codec.multipart.FilePart;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.List;

@Service("upload-service")
public class UploadDirectory implements IUploadDirectory, IUnpackingDirectory {
    private final ServiceBuildDirectoryPath buildDirectoryPath;

    @Autowired
    public UploadDirectory(ServiceBuildDirectoryPath buildDirectoryPath) {
        this.buildDirectoryPath = buildDirectoryPath;
    }

    public Mono<ResponseUploadDirectory> upload(
            List<String> parents,
            Flux<FilePart> directory
    ) throws NoSuchFileException {
        Path path = Path.of(buildDirectoryPath.build(parents));
        final ContainerUploadFile container = new ContainerUploadFile();
        final ResponseUploadDirectory dataSuccessTrue = new ResponseUploadDirectory(true);

        return directory.flatMap(
                it -> it.transferTo(
                        container.setFile(Path.of(path + Directory.SEPARATOR.get() + it.filename())))
                )
                .then(unpacking(container, path))
                .then(Mono.just(dataSuccessTrue));
    }
}
