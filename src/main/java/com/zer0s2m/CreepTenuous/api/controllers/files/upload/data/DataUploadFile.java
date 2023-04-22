package com.zer0s2m.CreepTenuous.api.controllers.files.upload.data;

import org.springframework.http.codec.multipart.FilePart;
import reactor.core.publisher.Flux;

import java.util.List;

public class DataUploadFile {
    private final Flux<FilePart> files;
    private final List<String> parents;

    public DataUploadFile(Flux<FilePart> files, List<String> parents) {
        this.files = files;
        this.parents = parents;
    }

    public Flux<FilePart> getFiles() {
        return files;
    }

    public List<String> getParents() {
        return parents;
    }
}
