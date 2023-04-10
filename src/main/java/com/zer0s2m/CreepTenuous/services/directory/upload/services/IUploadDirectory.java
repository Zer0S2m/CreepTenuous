package com.zer0s2m.CreepTenuous.services.directory.upload.services;

import com.zer0s2m.CreepTenuous.api.controllers.directory.upload.http.ResponseUploadDirectory;

import org.springframework.http.codec.multipart.FilePart;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.file.NoSuchFileException;
import java.util.List;

public interface IUploadDirectory {
    Mono<ResponseUploadDirectory> upload(List<String> parents, Flux<FilePart> directory) throws NoSuchFileException;
}
