package CreepTenuous.api.controllers.files.uploadFile;

import CreepTenuous.api.controllers.files.uploadFile.data.DataUploadFile;
import CreepTenuous.api.core.version.v1.V1APIController;
import CreepTenuous.services.directory.utils.check.CheckIsExistsDirectoryApi;
import CreepTenuous.services.files.uploadFile.service.impl.UploadFile;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestPart;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.nio.file.Paths;

@V1APIController
public class UploadFileApi implements CheckIsExistsDirectoryApi {
    @Autowired
    private UploadFile uploadFile;

    @PostMapping(value = "/file/upload")
    public Mono<String> upload(final @RequestPart("file") Flux<FilePart> file) throws IOException {
//        uploadFile.upload(data.getFiles(), data.getParents());
        return file.flatMap(it -> it.transferTo(Paths.get("/tmp/" + it.filename())))
                .then(Mono.just("OK"));
    }
}
