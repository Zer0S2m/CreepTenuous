package com.zer0s2m.CreepTenuous.services.directory.delete.services;

import java.nio.file.NoSuchFileException;
import java.util.List;

public interface IDeleteDirectory {
    void delete(List<String> parents, String name) throws NoSuchFileException;
}
