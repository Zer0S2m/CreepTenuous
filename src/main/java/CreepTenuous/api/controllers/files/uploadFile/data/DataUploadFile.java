package CreepTenuous.api.controllers.files.uploadFile.data;

import org.springframework.http.codec.multipart.FilePart;
import reactor.core.publisher.Mono;

import java.util.List;

public class DataUploadFile {
    private final Mono<FilePart> files;
    private final List<String> parents;

    public DataUploadFile(Mono<FilePart> files, List<String> parents) {
        this.files = files;
        this.parents = parents;
    }

    public Mono<FilePart> getFiles() {
        return files;
    }

    public List<String> getParents() {
        return parents;
    }
}
