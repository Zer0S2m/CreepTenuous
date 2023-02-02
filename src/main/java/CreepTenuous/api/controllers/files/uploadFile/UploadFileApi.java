package CreepTenuous.api.controllers.files.uploadFile;

import CreepTenuous.api.controllers.files.uploadFile.response.ResponseUploadFile;
import CreepTenuous.api.core.version.v1.V1APIController;
import CreepTenuous.services.directory.utils.check.CheckIsExistsDirectoryApi;
import CreepTenuous.services.files.uploadFile.service.impl.UploadFile;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.List;

@V1APIController
public class UploadFileApi implements CheckIsExistsDirectoryApi {
    @Autowired
    private UploadFile uploadFile;

    @PostMapping(value = "/file/upload")
    public Mono<ResponseUploadFile> upload(
            final @RequestPart("files") Flux<FilePart> files,
            final @RequestParam("parents") List<String> parents
    ) throws IOException {
        return uploadFile.upload(files, parents);
    }
}
