package CreepTenuous.services.files.downloadFile.service.impl;

import CreepTenuous.api.controllers.common.exceptions.NoSuchFileExistsException;
import CreepTenuous.services.directory.builderDirectory.enums.Directory;
import CreepTenuous.services.directory.utils.build.BuildDirectoryPath;
import CreepTenuous.services.files.downloadFile.service.IDownloadFile;
import CreepTenuous.services.files.downloadFile.service.containers.ContainerDownloadFile3;
import CreepTenuous.services.files.enums.ExceptionFile;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.mail.javamail.ConfigurableMimeFileTypeMap;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Service("download-file")
public class DownloadFile implements IDownloadFile {
    @Autowired
    private BuildDirectoryPath buildDirectoryPath;
    private final ConfigurableMimeFileTypeMap fileTypeMap = new ConfigurableMimeFileTypeMap();

    @Override
    public ContainerDownloadFile3<ByteArrayResource, String> download(
            List<String> parents,
            String filename
    ) throws IOException, NoSuchFileExistsException {
        Path path = Paths.get(buildDirectoryPath.build(parents));
        Path pathFile = Paths.get(path + Directory.SEPARATOR.get() + filename);

        if (!Files.exists(pathFile)) {
            throw new NoSuchFileExistsException(ExceptionFile.FILE_NOT_EXISTS.get());
        }

        return new ContainerDownloadFile3<>(
                new ByteArrayResource(Files.readAllBytes(pathFile)),
                fileTypeMap.getContentType(pathFile.toString()),
                filename
        );
    }

    @Override
    public HttpHeaders collectHeaders(ContainerDownloadFile3<ByteArrayResource, String> data) {
        final HttpHeaders headers = new HttpHeaders();

        headers.add(HttpHeaders.CACHE_CONTROL, "no-cache, no-store, must-revalidate");
        headers.add(HttpHeaders.PRAGMA, "no-cache");
        headers.add(HttpHeaders.EXPIRES, "0");
        headers.add(HttpHeaders.CONTENT_DISPOSITION, ContentDisposition
                .attachment().
                filename(data.getFilename())
                .build()
                .toString()
        );
        headers.add(HttpHeaders.CONTENT_TYPE, data.getMimeType());
        headers.add(HttpHeaders.CONTENT_LENGTH, String.valueOf(data.getByteContent().contentLength()));

        return headers;
    }
}
