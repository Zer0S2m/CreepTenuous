package CreepTenuous.services.common.collectRootPath.impl;

import CreepTenuous.components.RootPath;
import CreepTenuous.services.common.collectRootPath.ICollectRootPath;
import CreepTenuous.services.directory.manager.enums.Directory;
import CreepTenuous.providers.build.os.services.CheckIsExistsDirectoryService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;
import java.util.Objects;

@Service("collect-root-path")
public final class CollectRootPath implements ICollectRootPath, CheckIsExistsDirectoryService {
    private final RootPath rootPath;

    @Autowired
    public CollectRootPath(RootPath rootPath) {
        this.rootPath = rootPath;
    }

    @Override
    public String collect(String rawPath) throws NoSuchFileException {
        String path = this.rootPath.getRootPath();

        if (rawPath.length() != 0 && Objects.equals(rawPath.charAt(0), '/')) {
            path = path + Directory.SEPARATOR.get() + rawPath;
        } else {
            path = path + rawPath;
        }

        checkDirectory(Paths.get(path));

        return path;
    }
}
