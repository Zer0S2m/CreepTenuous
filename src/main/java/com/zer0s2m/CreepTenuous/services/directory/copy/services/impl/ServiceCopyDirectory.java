package com.zer0s2m.CreepTenuous.services.directory.copy.services.impl;

import com.zer0s2m.CreepTenuous.services.directory.copy.enums.MethodCopyDirectory;
import com.zer0s2m.CreepTenuous.services.directory.manager.enums.Directory;
import com.zer0s2m.CreepTenuous.services.directory.copy.services.ICopyDirectory;
import com.zer0s2m.CreepTenuous.providers.build.os.services.impl.ServiceBuildDirectoryPath;
import com.zer0s2m.CreepTenuous.providers.build.os.services.CheckIsExistsDirectoryService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

@Service("copy-directory")
public class ServiceCopyDirectory implements ICopyDirectory, CheckIsExistsDirectoryService {
    private final ServiceBuildDirectoryPath buildDirectoryPath;

    private Path newPath;

    @Autowired
    public ServiceCopyDirectory(ServiceBuildDirectoryPath buildDirectoryPath) {
        this.buildDirectoryPath = buildDirectoryPath;
    }

    @Override
    public void copy(
            List<String> parents,
            List<String> toParents,
            String nameDirectory,
            Integer method
    ) throws IOException {
        Path currentPath = Paths.get(buildDirectoryPath.build(parents) + Directory.SEPARATOR.get(), nameDirectory);
        checkDirectory(currentPath);

        if (Objects.equals(method, MethodCopyDirectory.FOLDER.getMethod())) {
            Path futurePath = Paths.get(buildDirectoryPath.build(toParents), nameDirectory);
            if (!Files.exists(futurePath)) {
                this.newPath = Files.createDirectory(futurePath);
            }
        } else {
            this.newPath = Paths.get(buildDirectoryPath.build(toParents));
        }

        try (Stream<Path> stream = Files.walk(currentPath)) {
            stream.forEach(target -> {
                try {
                    Files.copy(
                            target,
                            this.newPath.resolve(currentPath.relativize(target)),
                            StandardCopyOption.REPLACE_EXISTING
                    );
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        }
    }
}
