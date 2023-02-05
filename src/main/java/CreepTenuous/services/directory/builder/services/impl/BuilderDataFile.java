package CreepTenuous.services.directory.builder.services.impl;

import CreepTenuous.services.directory.builder.services.IBuilderDataFile;
import org.json.JSONObject;
import org.json.JSONArray;
import org.springframework.stereotype.Service;

import java.net.URLConnection;
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

        if (isFile) {
            String typeFile = URLConnection.guessContentTypeFromName(path.toString());
            if (typeFile == null) {
                obj.put("typeFile", false);
            } else {
                obj.put("typeFile", typeFile);
            }
        }

        readyFiles.put(obj);
    }
}
