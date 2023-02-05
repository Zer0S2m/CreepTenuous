package CreepTenuous.services.files.upload.services.impl;

import CreepTenuous.api.controllers.files.upload.response.ResponseUploadFile;
import CreepTenuous.services.directory.builder.enums.Directory;
import CreepTenuous.services.directory.utils.build.BuildDirectoryPath;
import CreepTenuous.services.files.upload.services.IUploadFile;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Service("upload-file")
public class UploadFile implements IUploadFile {
    @Autowired
    private BuildDirectoryPath buildDirectoryPath;

    @Override
    public Mono<ResponseUploadFile> upload(
            Flux<FilePart> files,
            List<String> parents
    ) throws IOException {
        final ResponseUploadFile dataSuccessTrue = new ResponseUploadFile(true);
        Path path = Paths.get(buildDirectoryPath.build(parents));
        return files.flatMap(
                it -> it.transferTo(Paths.get(
                        path + Directory.SEPARATOR.get() + it.filename())
                ))
                .then(Mono.just(dataSuccessTrue));
    }
}
