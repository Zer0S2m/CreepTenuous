package com.zer0s2m.creeptenuous.core.context.nio.file;

import com.zer0s2m.creeptenuous.core.context.ContextAtomicFileSystem;

import java.io.File;
import java.io.IOException;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.FileAttribute;
import java.util.HashMap;

/**
 * Atomic Mode Context Management with File System Operations. Interlayer between {@link Files}
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
     * A directory made available for applications that need a place to create temporary files.
     * <a href="https://refspecs.linuxfoundation.org/FHS_3.0/fhs/ch03s18.html">Documentation <b>tmp</b></a>
     */
    String tmpDirectory = "/tmp";

    String rootPathKey = "ROOT_PATH";

    String rootPath = System.getenv(rootPathKey);

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
     * Move or rename a file to a target file. Add data in context {@link ContextAtomicFileSystem}
     * of <b>atomic mode</b>
     * @param source the path to the file to move
     * @param target the path to the target file (may be associated with a different provider to the source path)
     * @param options options specifying how the move should be done
     * @return the path to the target file
     * @throws IOException if an I/O error occurs
     */
    static Path move(Path source, Path target, CopyOption... options) throws IOException {
        addOperationDataMove(source, target);
        return Files.move(source, target, options);
    }

    /**
     * Copy or rename a file to a target file. Add data in context {@link ContextAtomicFileSystem}
     * of <b>atomic mode</b>
     * @param source the path to the file to copy
     * @param target the path to the target file (may be associated with a different provider to the source path)
     * @param options options specifying how the move should be done
     * @return the path to the target file
     * @throws IOException if an I/O error occurs
     */
    static Path copy(Path source, Path target, CopyOption... options) throws IOException {
        addOperationDataCopy(target);
        return Files.copy(source, target, options);
    }

    /**
     * Deletes a file. Add data in context {@link ContextAtomicFileSystem} of <b>atomic mode</b>
     * @param path the path to the file to delete
     * @throws IOException if an I/O error occurs
     */
    static void delete(Path path) throws IOException {
        boolean isDirectory = Files.isDirectory(path);
        boolean isFile = Files.isRegularFile(path);
        addOperationDataDelete(path, transferToTmp(path), isDirectory, isFile);
    }

    /**
     * Moving file system object to <b>/tmp</b>
     * @param source the path to the file to transfer
     * @return the path to the target file
     * @throws IOException if an I/O error occurs
     */
    static private Path transferToTmp(Path source) throws IOException {
        Path directoryInTmp = Path.of(tmpDirectory, source.getFileName().toString());
        return Files.move(source, directoryInTmp, StandardCopyOption.REPLACE_EXISTING);
    }

    /**
     * Deletes the file or directory denoted by this abstract pathname.
     * If this pathname denotes a directory, then the directory must be empty in order to be deleted.
     * Add data in context {@link ContextAtomicFileSystem} of <b>atomic mode</b>
     * @param file source file
     */
    static void deleteNoException(File file) {
        try {
            delete(file.toPath());
        } catch (RuntimeException | IOException ignored) {
        }
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

    /**
     * Write operation data to atomic mode context
     * @param source the path source
     * @param target the path target
     */
    private static void addOperationDataMove(Path source, Path target) {
        HashMap<String, Object> operationData = new HashMap<>();

        String systemNameFile = target.getFileName().toString();

        operationData.put("operation", ContextAtomicFileSystem.Operations.MOVE);
        operationData.put("sourcePath", source);
        operationData.put("targetPath", target);

        contextAtomicFileSystem.addOperationData(systemNameFile, operationData);
    }

    /**
     * Write operation data to atomic mode context
     * @param target the path target
     */
    private static void addOperationDataCopy(Path target) {
        HashMap<String, Object> operationData = new HashMap<>();

        operationData.put("operation", ContextAtomicFileSystem.Operations.COPY);
        operationData.put("targetPath", target);

        contextAtomicFileSystem.addOperationData(target.getFileName().toString(), operationData);
    }

    /**
     * Write operation data to atomic mode context
     * @param source the path source
     * @param target the path target
     * @param isDirectory the path is directory
     * @param isFile the path is file
     */
    private static void addOperationDataDelete(Path source, Path target, boolean isDirectory, boolean isFile) {
        HashMap<String, Object> operationData = new HashMap<>();

        String systemNameFile = target.getFileName().toString();

        operationData.put("operation", ContextAtomicFileSystem.Operations.DELETE);
        operationData.put("sourcePath", source);
        operationData.put("targetPath", target);
        operationData.put("isDirectory", isDirectory);
        operationData.put("isFile", isFile);

        contextAtomicFileSystem.addOperationData(systemNameFile, operationData);
    }
}
