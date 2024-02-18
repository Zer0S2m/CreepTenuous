package com.zer0s2m.creeptenuous.core.balancer;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;

/**
 * File as defined files.
 */
public class FileSplit {

    private final Collection<Path> pathFiles = new ArrayList<>();

    public FileSplit(Collection<Path> pathFiles) {
        this.pathFiles.addAll(pathFiles);
    }

    public Collection<Path> getPathFiles() {
        return pathFiles;
    }

    @Override
    public String toString() {
        return super.toString() + "[sources=" + pathFiles + "]";
    }

    public void addPathFiles(Path sourcePart) {
        this.pathFiles.add(sourcePart);
    }

}
