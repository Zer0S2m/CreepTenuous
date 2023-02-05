package CreepTenuous.services.directory.downloadDirectory.impl;

import CreepTenuous.services.directory.builderDirectory.enums.Directory;
import CreepTenuous.services.directory.downloadDirectory.IDownloadDirectory;
import CreepTenuous.services.directory.downloadDirectory.ICollectZipDirectory;
import CreepTenuous.services.directory.utils.build.BuildDirectoryPath;
import CreepTenuous.services.directory.utils.check.CheckIsExistsDirectoryService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.nio.file.Path;

@Service("download-directory")
public class DownloadDirectory implements IDownloadDirectory, ICollectZipDirectory, CheckIsExistsDirectoryService {
    @Autowired
    private BuildDirectoryPath buildDirectoryPath;

    @Override
    public Mono<ResponseEntity<Resource>> download(
            List<String> parents,
            String directory
    ) throws IOException {
        Path path = Paths.get(buildDirectoryPath.build(parents) + Directory.SEPARATOR.get() + directory);
        checkDirectory(path);

        Path pathToZip = collectZip(path);
        ByteArrayResource contentBytes = new ByteArrayResource(Files.readAllBytes(pathToZip));

        return Mono.just(true)
                .then(deleteFileZip(pathToZip))
                .thenReturn(ResponseEntity.ok()
                        .headers(collectHeaders(pathToZip, contentBytes))
                        .body(contentBytes)
                );
    }

    @Override
    public HttpHeaders collectHeaders(Path path, ByteArrayResource data) {
        HttpHeaders headers = new HttpHeaders();

        headers.add(HttpHeaders.EXPIRES, "1");
        headers.add(HttpHeaders.CONTENT_DISPOSITION, ContentDisposition
                .attachment()
                .filename(String.valueOf(path.getFileName()))
                .build()
                .toString()
        );
        headers.add(HttpHeaders.CONTENT_TYPE, Directory.TYPE_APPLICATION_ZIP.get());
        headers.add(HttpHeaders.CONTENT_LENGTH, String.valueOf(data.contentLength()));

        return headers;
    }

    private Mono<Void> deleteFileZip(Path path) {
        return Mono.create(sink -> {
            path.toFile().delete();
            sink.success();
        });
    }
}
