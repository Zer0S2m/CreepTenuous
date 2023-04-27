package com.zer0s2m.CreepTenuous.services.files.create.services.impl;

import com.zer0s2m.CreepTenuous.providers.build.os.services.impl.ServiceBuildDirectoryPath;
import com.zer0s2m.CreepTenuous.services.directory.manager.enums.Directory;
import com.zer0s2m.CreepTenuous.services.files.create.containers.ContainerDataCreatedFile;
import com.zer0s2m.CreepTenuous.services.files.create.exceptions.NotFoundTypeFileException;
import com.zer0s2m.CreepTenuous.services.files.create.services.ICreateFile;

import com.zer0s2m.CreepTenuous.services.files.enums.TypeFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;

@Service("create-file")
public class ServiceCreateFile implements ICreateFile {
    private final ServiceBuildDirectoryPath buildDirectoryPath;

    @Autowired
    public ServiceCreateFile(ServiceBuildDirectoryPath buildDirectoryPath) {
        this.buildDirectoryPath = buildDirectoryPath;
    }

    @Override
    public ContainerDataCreatedFile create(
            List<String> parents,
            String nameFile,
            Integer typeFile
    ) throws NotFoundTypeFileException, IOException {
        checkTypeFile(typeFile);
        Path path = Paths.get(buildDirectoryPath.build(parents));
        conductor(path, nameFile, typeFile);

        String fullNameFile = nameFile + "." + TypeFile.getExtension(typeFile);
        Path fullPath = Path.of(path + Directory.SEPARATOR.get() + fullNameFile);

        return new ContainerDataCreatedFile(fullNameFile, fullPath);
    }
}
