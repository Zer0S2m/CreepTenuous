package CreepTenuous.api.controllers.directory.upload.forms;

import org.springframework.http.codec.multipart.FilePart;
import reactor.core.publisher.Flux;

import java.util.List;

public class FormUploadDirectoryApi {
    private final List<String> parents;
    private final Flux<FilePart> directory;

    public FormUploadDirectoryApi(List<String> parents, Flux<FilePart> directory) {
        this.parents = parents;
        this.directory = directory;
    }

    public List<String> getParents() {
        return parents;
    }

    public Flux<FilePart> getDirectory() {
        return directory;
    }
}
