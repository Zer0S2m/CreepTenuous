package com.zer0s2m.CreepTenuous.services.directory.manager.services.impl;

import com.zer0s2m.CreepTenuous.services.core.ServiceFileSystem;
import com.zer0s2m.CreepTenuous.services.directory.manager.containers.ContainerDataSystemFileObject;
import com.zer0s2m.CreepTenuous.services.directory.manager.services.IServiceBuilderDataFile;

import org.json.JSONObject;
import org.json.JSONArray;

import java.net.URLConnection;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

@ServiceFileSystem("build-data-file")
public class ServiceBuilderDataFile implements IServiceBuilderDataFile {
    /**
     * Get data system file objects
     * @param paths source directories
     * @return data system file objects
     */
    @Override
    public ContainerDataSystemFileObject build(ArrayList<List<Path>> paths) {
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

        return new ContainerDataSystemFileObject(readyFiles.toList(), pathsDirectory);
    }

    /**
     * Build json
     * @param path source
     * @param readyFiles array data
     * @param isFile is file
     * @param isDirectory is directory
     */
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
