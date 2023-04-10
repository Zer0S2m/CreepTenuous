package com.zer0s2m.CreepTenuous.services.directory.move.services;

import java.io.IOException;
import java.util.List;

public interface IMoveDirectory {
    void move(List<String> parents, List<String> toParents, String nameDirectory) throws IOException;
}
