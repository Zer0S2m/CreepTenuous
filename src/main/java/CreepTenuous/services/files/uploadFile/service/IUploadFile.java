package CreepTenuous.services.files.uploadFile.service;

import CreepTenuous.api.controllers.files.uploadFile.response.ResponseUploadFile;

import org.springframework.http.codec.multipart.FilePart;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.List;

public interface IUploadFile {
    Mono<ResponseUploadFile> upload(Flux<FilePart> files, List<String> parents) throws IOException;
}
