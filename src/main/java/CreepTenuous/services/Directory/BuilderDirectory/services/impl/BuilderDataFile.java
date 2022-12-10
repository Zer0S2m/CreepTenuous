package CreepTenuous.services.Directory.BuilderDirectory.services.impl;

import CreepTenuous.services.Directory.BuilderDirectory.services.IBuilderDataFile;
import org.json.JSONObject;
import org.json.JSONArray;
import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

@Service("build-data-file")
public class BuilderDataFile implements IBuilderDataFile {
    @Override
    public List<Object> build(ArrayList<List<Path>> paths) {
        JSONArray readyFiles = new JSONArray();

        for (Path path : paths.get(0)) {
            buildJSON(path, readyFiles, true, false);
        }

        for (Path path : paths.get(1)) {
            buildJSON(path, readyFiles, false, true);
        }

        return readyFiles.toList();
    }

    private void buildJSON(Path path, JSONArray readyFiles, boolean isFile, boolean isDirectory) {
        JSONObject obj = new JSONObject();
        obj.put("fileName", path.getFileName().toString());
        obj.put("isFile", isFile);
        obj.put("isDirectory", isDirectory);
        readyFiles.put(obj);
    }
}
