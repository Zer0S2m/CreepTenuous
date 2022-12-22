package CreepTenuous.components;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public final class RootPath {
    private final String rootPath;

    public RootPath(@Value("${ROOT_PATH}") String rootPath) {
        if (Objects.equals(rootPath.charAt(rootPath.length() - 1), "/")) {
            this.rootPath = rootPath.substring(0, rootPath.length() - 1);
        } else {
            this.rootPath = rootPath;
        }
    }

    public String getRootPath() {
        return this.rootPath;
    }
}
