package com.zer0s2m.creeptenuous.core.context.nio.file;

import com.zer0s2m.creeptenuous.core.context.ContextAtomicFileSystem;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.util.HashMap;

/**
 * Atomic mode context management through file system operations:
 * <ul>
 *     <li>Writes the underlying data to an atomic mode context
 *     {@link ContextAtomicFileSystem#getOperationsData()}</li>
 * </ul>
 */
public interface FilesContextAtomic {

    /**
     * Context for working with the file system in <b>atomic mode</b>
     */
    ContextAtomicFileSystem contextAtomicFileSystem = ContextAtomicFileSystem.getInstance();

    /**
     * Creating a file with record data about the operation in the context {@link ContextAtomicFileSystem}
     * of <b>atomic mode</b>
     * @param path the path to the file to create
     * @param attrs an optional list of file attributes to set atomically when creating the file
     * @return the file
     * @throws IOException if an I/O error occurs or the parent directory does not exist
     */
    static Path createFile(Path path, FileAttribute<?>... attrs) throws IOException {
        addOperationDataCreate(path);
        return Files.createFile(path, attrs);
    }

    /**
     * Creating a directory with record data about the operation in the context {@link ContextAtomicFileSystem}
     * of <b>atomic mode</b>
     * @param dir the directory to create
     * @param attrs an optional list of file attributes to set atomically when creating the directory
     * @return the directory
     * @throws IOException if an I/O error occurs or the parent directory does not exist
     */
    static Path createDirectory(Path dir, FileAttribute<?>... attrs) throws IOException {
        addOperationDataCreate(dir);
        return Files.createDirectory(dir, attrs);
    }

    /**
     * Write operation data to atomic mode context
     * @param path the path
     */
    private static void addOperationDataCreate(Path path) {
        HashMap<String, Object> operationData = new HashMap<>();

        String systemNameFile = path.getFileName().toString();

        operationData.put("operation", ContextAtomicFileSystem.Operations.CREATE);
        operationData.put("systemName", systemNameFile);
        operationData.put("systemPath", path);

        contextAtomicFileSystem.addOperationData(systemNameFile, operationData);
    }
}
