package CreepTenuous.common.InfoFileAndDirectory.services;

import CreepTenuous.Api.common.InfoFileAndDirectory.data.DataInfoAndDirectoryApi;
import CreepTenuous.Directory.BuilderDirectory.enums.Directory;
import CreepTenuous.common.InfoFileAndDirectory.IInfoFileAndDirectory;

import CreepTenuous.Directory.utils.build.BuildDirectoryPath;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.stream.Stream;

@Service("get-info-file-and-directory")
public class InfoFileAndDirectory implements IInfoFileAndDirectory {
    public DataInfoAndDirectoryApi collect(String[] parents) throws NoSuchFileException {
        String path = BuildDirectoryPath.build(parents);
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
        long size = 0;
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