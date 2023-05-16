package com.zer0s2m.creeptenuous.services.system.impl;

import com.zer0s2m.creeptenuous.common.annotations.ServiceFileSystem;
import com.zer0s2m.creeptenuous.common.containers.ContainerDataCreateFile;
import com.zer0s2m.creeptenuous.common.enums.TypeFile;
import com.zer0s2m.creeptenuous.common.exceptions.NotFoundTypeFileException;
import com.zer0s2m.creeptenuous.core.annotations.AtomicFileSystem;
import com.zer0s2m.creeptenuous.core.annotations.AtomicFileSystemExceptionHandler;
import com.zer0s2m.creeptenuous.core.annotations.CoreServiceFileSystem;
import com.zer0s2m.creeptenuous.core.handlers.AtomicServiceFileSystem;
import com.zer0s2m.creeptenuous.services.system.core.ServiceBuildDirectoryPath;
import com.zer0s2m.creeptenuous.services.system.ServiceCreateFile;
import com.zer0s2m.creeptenuous.services.system.core.Distribution;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;

@ServiceFileSystem("service-create-file")
@CoreServiceFileSystem(method = "create")
public class ServiceCreateFileImpl implements ServiceCreateFile, AtomicServiceFileSystem {
    private final ServiceBuildDirectoryPath buildDirectoryPath;

    @Autowired
    public ServiceCreateFileImpl(ServiceBuildDirectoryPath buildDirectoryPath) {
        this.buildDirectoryPath = buildDirectoryPath;
    }

    @Override
    @AtomicFileSystem(
            name = "create-file",
            handlers = { @AtomicFileSystemExceptionHandler(exception = NoSuchFileException.class) }
    )
    public ContainerDataCreateFile create(
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
        Path fullPath = Path.of(path.toString(), fullNameFile);
        Path newFullPath = Path.of(path.toString(), newFullNameFile);

        return new ContainerDataCreateFile(fullNameFile, newFullNameFile, fullPath, newFullPath);
    }
}
