package CreepTenuous.api.controllers.directory.upload;

import CreepTenuous.api.controllers.directory.upload.http.ResponseUploadDirectory;
import CreepTenuous.api.core.version.v1.V1APIController;
import CreepTenuous.services.directory.upload.services.impl.UploadDirectory;
import CreepTenuous.providers.build.os.services.CheckIsExistsDirectoryApi;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RequestParam;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.file.NoSuchFileException;
import java.util.List;

@V1APIController
public class UploadDirectoryApi implements CheckIsExistsDirectoryApi {
    private final UploadDirectory uploadDirectory;

    @Autowired
    public UploadDirectoryApi(UploadDirectory uploadDirectory) {
        this.uploadDirectory = uploadDirectory;
    }

    @PostMapping(path = "/directory/upload")
    public final Mono<ResponseUploadDirectory> upload(
            final @Nullable @RequestParam("parents") List<String> parents,
            final @RequestPart("directory") Flux<FilePart> directory
    ) throws NoSuchFileException {
        return uploadDirectory.upload(parents, directory);
    }
}
