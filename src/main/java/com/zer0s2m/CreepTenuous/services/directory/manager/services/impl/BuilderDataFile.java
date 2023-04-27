package com.zer0s2m.CreepTenuous.services.directory.manager.services.impl;

import com.zer0s2m.CreepTenuous.providers.redis.models.DirectoryRedis;
import com.zer0s2m.CreepTenuous.services.directory.manager.containers.ContainerDataFiles;
import com.zer0s2m.CreepTenuous.services.directory.manager.services.IBuilderDataFile;

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
    public ContainerDataFiles build(ArrayList<List<Path>> paths) {
        JSONArray readyFiles = new JSONArray();
        List<String> pathsDirectory = new ArrayList<>();

        for (Path path : paths.get(0)) {
            buildJSON(path, readyFiles, true, false);
            pathsDirectory.add(path.getFileName().toString());
        }

        for (Path path : paths.get(1)) {
            buildJSON(path, readyFiles, false, true);
            pathsDirectory.add(path.getFileName().toString());
        }

        return new ContainerDataFiles(readyFiles.toList(), pathsDirectory);
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

    @Override
    public List<Object> build(List<DirectoryRedis> redisList) {
        JSONArray readyFiles = new JSONArray();

        redisList.forEach((objRedis) -> {
            JSONObject objJson = new JSONObject();
            objJson.put("fileName", objRedis.getNameDirectory());
            objJson.put("path", objRedis.getPathDirectory());
            objJson.put("isDirectory", objRedis.getIsDirectory());
            objJson.put("isFile", objRedis.getIsFile());
            readyFiles.put(objJson);
        });

        return readyFiles.toList();
    }
}
