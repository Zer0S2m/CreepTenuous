package com.zer0s2m.creeptenuous.services.system.impl;

import com.zer0s2m.creeptenuous.common.annotations.ServiceFileSystem;
import com.zer0s2m.creeptenuous.common.containers.ContainerDataCreateFile;
import com.zer0s2m.creeptenuous.common.enums.TypeFile;
import com.zer0s2m.creeptenuous.common.exceptions.NotFoundTypeFileException;
import com.zer0s2m.creeptenuous.core.atomic.annotations.AtomicFileSystem;
import com.zer0s2m.creeptenuous.core.atomic.annotations.AtomicFileSystemExceptionHandler;
import com.zer0s2m.creeptenuous.core.atomic.annotations.CoreServiceFileSystem;
import com.zer0s2m.creeptenuous.core.atomic.context.ContextAtomicFileSystem;
import com.zer0s2m.creeptenuous.core.atomic.handlers.impl.ServiceFileSystemExceptionHandlerOperationCreate;
import com.zer0s2m.creeptenuous.core.atomic.services.AtomicServiceFileSystem;
import com.zer0s2m.creeptenuous.services.system.core.ServiceBuildDirectoryPath;
import com.zer0s2m.creeptenuous.services.system.ServiceCreateFile;
import com.zer0s2m.creeptenuous.core.atomic.services.Distribution;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.nio.file.*;
import java.util.HashMap;
import java.util.List;

/**
 * Service for serving the creation of files of different formats.
 */
@ServiceFileSystem("service-create-file")
@CoreServiceFileSystem(method = "create")
public class ServiceCreateFileImpl implements ServiceCreateFile, AtomicServiceFileSystem {

    private final ServiceBuildDirectoryPath buildDirectoryPath;

    private final ContextAtomicFileSystem contextAtomicFileSystem = ContextAtomicFileSystem.getInstance();

    @Autowired
    public ServiceCreateFileImpl(ServiceBuildDirectoryPath buildDirectoryPath) {
        this.buildDirectoryPath = buildDirectoryPath;
    }

    /**
     * Create file with specific format
     * @param parents system path part directories
     * @param nameFile file name
     * @param typeFile type file {@link TypeFile}
     * @return information about the created file
     * @throws NotFoundTypeFileException file format not found to generate it
     * @throws IOException if an I/O error occurs or the parent directory does not exist
     */
    @Override
    @AtomicFileSystem(
            name = "create-file",
            handlers = {
                    @AtomicFileSystemExceptionHandler(
                            exception = NoSuchFileException.class,
                            handler = ServiceFileSystemExceptionHandlerOperationCreate.class,
                            operation = ContextAtomicFileSystem.Operations.CREATE
                    )
            }
    )
    public ContainerDataCreateFile create(List<String> parents, String nameFile, Integer typeFile)
            throws NotFoundTypeFileException, IOException {
        checkTypeFile(typeFile);
        String newSystemNameFile = Distribution.getUUID();
        Path path = Paths.get(buildDirectoryPath.build(parents));
        conductor(path, newSystemNameFile, typeFile);

        String realNameFile = nameFile + "." + TypeFile.getExtension(typeFile);
        String systemNameFile = newSystemNameFile + "." + TypeFile.getExtension(typeFile);
        Path realPath = Path.of(path.toString(), realNameFile);
        Path systemPath = Path.of(path.toString(), systemNameFile);

        this.buildOperationData(realNameFile, systemNameFile, realPath, systemPath, newSystemNameFile);

        return new ContainerDataCreateFile(realNameFile, systemNameFile, realPath, systemPath);
    }

    /**
     * Push data in context atomic model
     * @param realNameFile real name file
     * @param systemNameFile system new file (generate uuid and type file)
     * @param realPath real path
     * @param systemPath system path
     * @param newSystemNameFile system file name (uuid)
     */
    private void buildOperationData(
            String realNameFile,
            String systemNameFile,
            Path realPath,
            Path systemPath,
            String newSystemNameFile
    ) {
        HashMap<String, Object> operationData = new HashMap<>();
        operationData.put("_class", this.getClass().getCanonicalName());
        operationData.put("operation", ContextAtomicFileSystem.Operations.CREATE);
        operationData.put("realName", realNameFile);
        operationData.put("systemName", systemNameFile);
        operationData.put("realPath", realPath);
        operationData.put("systemPath", systemPath);
        contextAtomicFileSystem.addOperationData(newSystemNameFile, operationData);
    }

}
