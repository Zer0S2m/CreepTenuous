package CreepTenuous.api.controllers.files.download.data;

import org.springframework.lang.Nullable;

import java.util.List;

public record DataDownloadFile(List<String> parents, String filename) {
    public DataDownloadFile(@Nullable List<String> parents, @Nullable String filename) {
        this.parents = parents;
        this.filename = filename;
    }
}
