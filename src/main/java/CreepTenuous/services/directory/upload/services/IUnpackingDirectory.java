package CreepTenuous.services.directory.upload.services;

import CreepTenuous.services.directory.builder.enums.Directory;
import CreepTenuous.services.directory.upload.threads.ThreadUnpackingDirectory;

import reactor.core.publisher.Mono;

public interface IUnpackingDirectory {
    default Mono<Void> unpacking() {
        return Mono.create(sink -> {
            ThreadUnpackingDirectory threadUnpacking = new ThreadUnpackingDirectory(
                    Directory.THREAD_NAME_UNPACKING_DIRECTORY.get()
            );
            threadUnpacking.start();
            sink.success();
        });
    }
}
