package com.zer0s2m.CreepTenuous.services.files.create.services.impl;

import com.zer0s2m.CreepTenuous.providers.build.os.core.Distribution;
import com.zer0s2m.CreepTenuous.providers.build.os.services.impl.ServiceBuildDirectoryPath;
import com.zer0s2m.CreepTenuous.services.core.Directory;
import com.zer0s2m.CreepTenuous.services.core.ServiceFileSystem;
import com.zer0s2m.CreepTenuous.services.files.create.containers.ContainerDataCreatedFile;
import com.zer0s2m.CreepTenuous.services.files.create.exceptions.NotFoundTypeFileException;
import com.zer0s2m.CreepTenuous.services.files.create.services.ICreateFile;

import com.zer0s2m.CreepTenuous.services.core.TypeFile;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;

@ServiceFileSystem("create-file")
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
        String newNameFile = Distribution.getUUID();
        Path path = Paths.get(buildDirectoryPath.build(parents));
        conductor(path, newNameFile, typeFile);

        String fullNameFile = nameFile + "." + TypeFile.getExtension(typeFile);
        String newFullNameFile = newNameFile + "." + TypeFile.getExtension(typeFile);
        Path fullPath = Path.of(path + Directory.SEPARATOR.get() + fullNameFile);
        Path newFullPath = Path.of(path + Directory.SEPARATOR.get() + newFullNameFile);

        return new ContainerDataCreatedFile(fullNameFile, newFullNameFile, fullPath, newFullPath);
    }
}
