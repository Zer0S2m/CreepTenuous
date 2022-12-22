package CreepTenuous.services.common.collectRootPath.impl;

import CreepTenuous.components.RootPath;
import CreepTenuous.services.common.collectRootPath.ICollectRootPath;
import CreepTenuous.services.directory.builderDirectory.enums.Directory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service("collect-root-path")
public final class CollectRootPath implements ICollectRootPath {
    @Autowired
    private RootPath rootPath;

    @Override
    public String collect(String rawPath) {
        String path = this.rootPath.getRootPath();

        if (rawPath.length() != 0 && Objects.equals(rawPath.charAt(0), "/")) {
            return path + Directory.SEPARATOR.get() + rawPath;
        } else {
            return path + rawPath;
        }
    }
}
