package CreepTenuous.services.directory.utils.build;

import CreepTenuous.services.directory.builderDirectory.enums.Directory;

public class BuildDirectoryPath {
    static public String build(String[] parents) {
        StringBuilder rawDirectory = new StringBuilder();
        for (String part : parents) {
            rawDirectory.append(Directory.SEPARATOR.get()).append(part);
        }
        return rawDirectory.toString();
    }
}
