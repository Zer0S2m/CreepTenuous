package CreepTenuous.services.files.upload.services;

import CreepTenuous.api.controllers.files.upload.response.ResponseUploadFile;

import org.springframework.http.codec.multipart.FilePart;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.List;

public interface IUploadFile {
    Mono<ResponseUploadFile> upload(Flux<FilePart> files, List<String> parents) throws IOException;
}
