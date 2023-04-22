package com.zer0s2m.CreepTenuous.services.files.delete.services;

import com.zer0s2m.CreepTenuous.api.controllers.common.exceptions.NoSuchFileExistsException;

import java.io.IOException;
import java.util.List;

public interface IDeleteFile {
    void delete(String nameFile, List<String> parents) throws IOException, NoSuchFileExistsException;
}
