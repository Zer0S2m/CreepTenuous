package com.zer0s2m.creeptenuous.core.services.impl;

import com.zer0s2m.creeptenuous.common.annotations.ServiceFileSystem;
import com.zer0s2m.creeptenuous.common.containers.ContainerDataSystemFileObject;
import com.zer0s2m.creeptenuous.core.services.ServiceBuilderDataFileSystemObject;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;
import org.json.JSONArray;

import java.net.URLConnection;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Service for collecting information about the directory and its objects
 */
@ServiceFileSystem("service-build-data-file-system-object")
public class ServiceBuilderDataFileSystemObjectImpl implements ServiceBuilderDataFileSystemObject {

    /**
     * Get data system file objects
     * @param paths source directories
     * @return data system file objects
     */
    @Override
    public ContainerDataSystemFileObject build(@NotNull List<List<Path>> paths) {
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
    private void buildJSON(@NotNull Path path, JSONArray readyFiles, boolean isFile, boolean isDirectory) {
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
