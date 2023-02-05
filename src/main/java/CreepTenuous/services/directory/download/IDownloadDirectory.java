package CreepTenuous.services.directory.download;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public interface IDownloadDirectory {
     Mono<ResponseEntity<Resource>> download(
             List<String> parents,
             String directory
     ) throws IOException;
     HttpHeaders collectHeaders(Path path, ByteArrayResource data);
}
