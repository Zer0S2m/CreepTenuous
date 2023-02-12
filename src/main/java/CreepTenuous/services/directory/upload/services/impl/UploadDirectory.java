package CreepTenuous.services.directory.upload.services.impl;

import CreepTenuous.api.controllers.directory.upload.response.ResponseUploadDirectory;
import CreepTenuous.services.directory.builder.enums.Directory;
import CreepTenuous.services.directory.upload.containers.ContainerUploadFile;
import CreepTenuous.services.directory.upload.services.IUploadDirectory;
import CreepTenuous.services.directory.upload.services.IUnpackingDirectory;
import CreepTenuous.services.directory.utils.build.BuildDirectoryPath;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.http.codec.multipart.FilePart;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Service("upload-service")
public class UploadDirectory implements IUploadDirectory, IUnpackingDirectory {
    @Autowired
    private BuildDirectoryPath buildDirectoryPath;

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