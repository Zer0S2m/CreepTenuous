package com.zer0s2m.CreepTenuous.services.directory.copy.services;

import java.io.IOException;
import java.util.List;

public interface ICopyDirectory {
    void copy(List<String> parents, List<String> toParents, String nameDirectory) throws IOException;
}