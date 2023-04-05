package CreepTenuous.services.common.infoFileAndDirectory.impl;

import CreepTenuous.api.controllers.common.infoFileAndDirectory.data.DataInfoAndDirectoryApi;
import CreepTenuous.services.directory.manager.enums.Directory;
import CreepTenuous.services.common.infoFileAndDirectory.IInfoFileAndDirectory;
import CreepTenuous.providers.build.os.services.impl.BuildDirectoryPath;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.List;
import java.util.stream.Stream;

@Service("get-info-file-and-directory")
public class InfoFileAndDirectory implements IInfoFileAndDirectory {
    private final BuildDirectoryPath buildDirectoryPath;

    @Autowired
    public InfoFileAndDirectory(BuildDirectoryPath buildDirectoryPath) {
        this.buildDirectoryPath = buildDirectoryPath;
    }

    @Override
    public DataInfoAndDirectoryApi collect(List<String> parents) throws NoSuchFileException {
        String path = buildDirectoryPath.build(parents);
        File file = new File(path);
        long size;
        if (file.isFile()) {
            size = getFileSize(file);
        } else {
            size = getDirectorySize(path);
        }
        return new DataInfoAndDirectoryApi(
                file.isHidden(),
                new Date(file.lastModified()),
                size
        );
    }

    private static long getFileSize(File file) {
        return file.length() / (1024 * 1024);
    }

    private static long getDirectorySize(String path) throws NoSuchFileException {
        long size;
        try (Stream<Path> walk = Files.walk(Paths.get(path))) {
            size = walk
                    .filter(Files::isRegularFile)
                    .mapToLong(p -> {
                        try {
                            return Files.size(p);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    })
                    .sum();
        } catch (IOException e) {
            throw new NoSuchFileException(Directory.NOT_FOUND_DIRECTORY.get());
        }

        return size / (1024 * 1024);
    }
}
