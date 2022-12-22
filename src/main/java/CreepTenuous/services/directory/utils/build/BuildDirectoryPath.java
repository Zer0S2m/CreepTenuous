package CreepTenuous.services.directory.utils.build;

import CreepTenuous.services.common.collectRootPath.impl.CollectRootPath;
import CreepTenuous.services.directory.builderDirectory.enums.Directory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("build-directory-path")
public class BuildDirectoryPath {
    @Autowired
    private CollectRootPath collectRootPath;

    public String build(List<String> parents) {
        StringBuilder rawDirectory = new StringBuilder();
        for (String part : parents) {
            rawDirectory.append(Directory.SEPARATOR.get()).append(part);
        }

        return collectRootPath.collect(rawDirectory.toString());
    }
}
