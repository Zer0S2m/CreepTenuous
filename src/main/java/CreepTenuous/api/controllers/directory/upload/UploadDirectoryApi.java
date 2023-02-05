package CreepTenuous.api.controllers.directory.upload;

import CreepTenuous.api.controllers.directory.upload.response.ResponseUploadDirectory;
import CreepTenuous.api.core.version.v1.V1APIController;
import CreepTenuous.services.directory.upload.services.impl.UploadDirectory;
import CreepTenuous.services.directory.utils.check.CheckIsExistsDirectoryApi;

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
    @Autowired
    private UploadDirectory uploadDirectory;

    @PostMapping(path = "/directory/upload")
    public final Mono<ResponseUploadDirectory> upload(
            @Nullable @RequestParam("parents") List<String> parents,
            @Nullable @RequestPart("directory") Flux<FilePart> directory
    ) throws NoSuchFileException {
        return uploadDirectory.upload(parents, directory);
    }
}
