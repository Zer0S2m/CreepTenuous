package com.zer0s2m.creeptenuous.core.context;

import com.zer0s2m.creeptenuous.core.annotations.AtomicFileSystemExceptionHandler;
import com.zer0s2m.creeptenuous.core.handlers.ServiceFileSystemExceptionHandler;
import com.zer0s2m.creeptenuous.core.handlers.impl.ServiceFileSystemExceptionHandlerOperationCreate;
import com.zer0s2m.creeptenuous.core.handlers.impl.ServiceFileSystemExceptionHandlerOperationMove;
import com.zer0s2m.creeptenuous.core.handlers.impl.ServiceFileSystemExceptionHandlerOperationCopy;
import com.zer0s2m.creeptenuous.core.handlers.impl.ServiceFileSystemExceptionHandlerOperationDelete;
import com.zer0s2m.creeptenuous.core.handlers.impl.ServiceFileSystemExceptionHandlerOperationUpload;
import com.zer0s2m.creeptenuous.core.handlers.impl.ServiceFileSystemExceptionHandlerOperationDownload;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;

/**
 * Context for storing and processing data associated with any operations directly with the file system
 * <p>Introduces context for <b>atomic filesystem mode</b></p>
 * <p>{@link ServiceFileSystemExceptionHandler} class to use file system object handling</p>
 */
public final class ContextAtomicFileSystem {

    Logger logger = LoggerFactory.getLogger(ContextAtomicFileSystem.class);

    private final String keyClassName = "_class";

    /**
     * <p> File system interaction data</p>
     * <p>
     *     1) Used in handler {@link ServiceFileSystemExceptionHandlerOperationCreate}.
     *     Basic key names and value types for operation {@link ContextAtomicFileSystem.Operations#CREATE}:<br>
     *     <ul>
     *         <li><b>_class</b> - {@link Class#getCanonicalName()} (<b><u>required</u></b>)</li>
     *         <li><b>operation</b> - {@link Operations} (<b><u>required</u></b>)</li>
     *         <li><b>systemName</b> - {@link String}</li>
     *         <li><b>systemPath</b> - {@link java.nio.file.Path}</li>
     *     </ul>
     * </p>
     * Example:
     * <pre>{@code
     * HashMap<String, Object> operationData = new HashMap<>();
     * operationData.put("_class", Class.class.getCanonicalName());
     * operationData.put("operation", Operations.CREATE);
     * operationData.put("systemName", systemNameFile);
     * operationData.put("systemPath", Path.of(uri));
     * contextAtomicFileSystem.addOperationData(systemNameFile, operationData);
     * }</pre>
     *
     * <p>
     *     2) Used in handler {@link ServiceFileSystemExceptionHandlerOperationMove}
     *     Basic key names and value types for operation {@link ContextAtomicFileSystem.Operations#MOVE}:<br>
     *     <ul>
     *         <li><b>_class</b> - {@link Class#getCanonicalName()} (<b><u>required</u></b>)</li>
     *         <li><b>operation</b> - {@link Operations} (<b><u>required</u></b>)</li>
     *         <li><b>sourcePath</b> - {@link java.nio.file.Path}</li>
     *         <li><b>targetPath</b> - {@link java.nio.file.Path}</li>
     *     </ul>
     * </p>
     * Example:
     * <pre>{@code
     * HashMap<String, Object> operationData = new HashMap<>();
     * operationData.put("_class", Class.class.getCanonicalName());
     * operationData.put("operation", Operations.MOVE);
     * operationData.put("sourcePath", Path.of(uri));
     * operationData.put("targetPath", Path.of(uri));
     * contextAtomicFileSystem.addOperationData(systemNameFile, operationData);
     * }</pre>
     *
     * <p>
     *     3) Used in handler {@link ServiceFileSystemExceptionHandlerOperationCopy}
     *     Basic key names and value types for operation {@link ContextAtomicFileSystem.Operations#COPY}:<br>
     *     <ul>
     *         <li><b>_class</b> - {@link Class#getCanonicalName()} (<b><u>required</u></b>)</li>
     *         <li><b>operation</b> - {@link Operations} (<b><u>required</u></b>)</li>
     *         <li><b>targetPath</b> - {@link java.nio.file.Path}</li>
     *     </ul>
     * </p>
     * Example:
     * <pre>{@code
     * HashMap<String, Object> operationData = new HashMap<>();
     * operationData.put("_class", Class.class.getCanonicalName());
     * operationData.put("operation", Operations.COPY);
     * operationData.put("targetPath", Path.of(uri));
     * contextAtomicFileSystem.addOperationData(systemNameFile, operationData);
     * }</pre>
     *
     * <p>
     *     4) Used in handler {@link ServiceFileSystemExceptionHandlerOperationDelete}
     *     Basic key names and value types for operation {@link ContextAtomicFileSystem.Operations#DELETE}:<br>
     *     <ul>
     *         <li><b>_class</b> - {@link Class#getCanonicalName()} (<b><u>required</u></b>)</li>
     *         <li><b>operation</b> - {@link Operations} (<b><u>required</u></b>)</li>
     *         <li><b>targetPath</b> - {@link java.nio.file.Path}</li>
     *         <li><b>sourcePath</b> - {@link java.nio.file.Path}</li>
     *         <li><b>isDirectory</b> - {@link Boolean}</li>
     *         <li><b>isFile</b> - {@link Boolean}</li>
     *     </ul>
     * </p>
     * Example:
     * <pre>{@code
     * HashMap<String, Object> operationData = new HashMap<>();
     * operationData.put("_class", Class.class.getCanonicalName());
     * operationData.put("operation", Operations.DELETE);
     * operationData.put("sourcePath", Path.of(uri));
     * operationData.put("targetPath", Path.of(uri));
     * operationData.put("isDirectory", Files.isDirectory(uri));
     * operationData.put("isFile", Files.isRegularFile(uri));
     * contextAtomicFileSystem.addOperationData(systemNameFile, operationData);
     * }</pre>
     *
     * <p>
     *     5) Used in handler {@link ServiceFileSystemExceptionHandlerOperationUpload}
     *     Basic key names and value types for operation {@link ContextAtomicFileSystem.Operations#UPLOAD}:<br>
     *     <ul>
     *         <li><b>_class</b> - {@link Class#getCanonicalName()} (<b><u>required</u></b>)</li>
     *         <li><b>operation</b> - {@link Operations} (<b><u>required</u></b>)</li>
     *         <li><b>targetPath</b> - {@link java.nio.file.Path}</li>
     *     </ul>
     * </p>
     * Example:
     * <pre>{@code
     * HashMap<String, Object> operationData = new HashMap<>();
     * operationData.put("_class", Class.class.getCanonicalName());
     * operationData.put("operation", Operations.UPLOAD);
     * operationData.put("targetPath", Path.of(uri));
     * contextAtomicFileSystem.addOperationData(systemNameFile, operationData);
     * }</pre>
     *
     * <p>
     *     5) Used in handler {@link ServiceFileSystemExceptionHandlerOperationDownload}
     *     Basic key names and value types for operation {@link ContextAtomicFileSystem.Operations#DOWNLOAD}:<br>
     *     <ul>
     *         <li><b>_class</b> - {@link Class#getCanonicalName()} (<b><u>required</u></b>)</li>
     *         <li><b>operation</b> - {@link Operations} (<b><u>required</u></b>)</li>
     *         <li><b>sourcePath</b> - {@link java.nio.file.Path}</li>
     *     </ul>
     * </p>
     * Example:
     * <pre>{@code
     * HashMap<String, Object> operationData = new HashMap<>();
     * operationData.put("_class", Class.class.getCanonicalName());
     * operationData.put("operation", Operations.DOWNLOAD);
     * operationData.put("targetPath", Path.of(uri));
     * contextAtomicFileSystem.addOperationData(systemNameFile, operationData);
     * }</pre>
     *
     * <p><u>You have the right to manage this data as you wish.</u></p><br>
     *
     * <b>Key</b> - unique file system object name (uuid)
     * <p>
     *     <b>Value</b> - data in file system object (custom data) <b><u>use at your own risk</u></b>
     * </p>
     */
    private final HashMap<String, HashMap<String, Object>> operationsData = new HashMap<>();

    public enum Operations {
        DELETE,
        CREATE,
        UPLOAD,
        DOWNLOAD,
        COPY,
        MOVE
    }

    private static ContextAtomicFileSystem instance;

    private ContextAtomicFileSystem() { }

    public static synchronized ContextAtomicFileSystem getInstance() {
        if (instance == null) {
            instance = new ContextAtomicFileSystem();
        }
        return instance;
    }

    /**
     * Add operation data object file system
     * @param key unique file system object name (uuid)
     * @param value custom data in file system object, example: {@link #operationsData}
     */
    public void addOperationData(String key, @NotNull HashMap<String, Object> value) {
        final String keyOperation = "operation";

        Class<?> clsEnumOperation = value.get(keyOperation).getClass();
        Class<?> superClsEnumOperation = clsEnumOperation.getSuperclass();
        assert clsEnumOperation.isEnum() || (superClsEnumOperation != null && superClsEnumOperation.isEnum()) :
                String.format("Invalid operation (key [%s]) format [%s]", keyOperation, superClsEnumOperation);

        this.operationsData.put(key, value);
    }

    /**
     * Get operation data
     * @param key unique file system object name (uuid
     * @return operation custom data (copied) {@link #operationsData}
     */
    @Contract("_ -> new")
    public @NotNull HashMap<String, Object> getOperationData(String key) {
        return new HashMap<>(operationsData.get(key));
    }

    /**
     * Get data
     * @return operation custom data (copied) {@link #operationsData}
     */
    @Contract(value = " -> new", pure = true)
    public @NotNull HashMap<String, HashMap<String, Object>> getOperationsData() {
        return new HashMap<>(operationsData);
    }

    /**
     * Get data
     * @param className caller class from method
     * @return operation custom data (copied) {@link #operationsData}
     */
    public @NotNull HashMap<String, HashMap<String, Object>> getOperationsData(String className) {
        HashMap<String, HashMap<String, Object>> illusionOperationData = new HashMap<>();
        operationsData.forEach((key, value) -> {
            if (value.get(keyClassName).equals(className)) {
                illusionOperationData.put(key, value);
            }
        });

        return illusionOperationData;
    }

    /**
     * Handling file system objects (if an exception was raised and included
     * in handling {@link AtomicFileSystemExceptionHandler}) for atomic mode
     * @param operation handle operation
     * @param objectOperationData object data file system
     */
    public void handleOperation(Operations operation, String objectOperationData) {
        writeOperationDataLogger(operation, objectOperationData);
        cleanOperationData(objectOperationData);
    }

    /**
     * Write log
     * @param operation operation handle
     * @param objectOperationData object data file system
     */
    private void writeOperationDataLogger(@NotNull Operations operation, String objectOperationData) {
        final String baseLog = "Run operation [%s] context atomic mode file system object [%s]";
        if (operation.equals(Operations.CREATE)) {
            logger.info(String.format(baseLog, "create", objectOperationData));
        } else if (operation.equals(Operations.DELETE)) {
            logger.info(String.format(baseLog, "delete", objectOperationData));
        } else if (operation.equals(Operations.UPLOAD)) {
            logger.info(String.format(baseLog, "upload", objectOperationData));
        } else if (operation.equals(Operations.DOWNLOAD)) {
            logger.info(String.format(baseLog, "download", objectOperationData));
        } else if (operation.equals(Operations.COPY)) {
            logger.info(String.format(baseLog, "copy", objectOperationData));
        } else if (operation.equals(Operations.MOVE)) {
            logger.info(String.format(baseLog, "move", objectOperationData));
        }
    }

    /**
     * Clearing file system object data
     * @param key object data file system (key)
     */
    private void cleanOperationData(String key) {
        this.operationsData.remove(key);
    }

    /**
     * Clear context by operation type
     * @param operation operation handle
     */
    public void clearOperationsData(Operations operation) {
        this.operationsData.forEach((key, operationData) -> {
            if (operation.equals(operationData.get("operation"))) {
                this.operationsData.remove(key);
            }
        });
    }

    /**
     * Clear all context
     */
    public void clearOperationsData() {
        operationsData.clear();
    }

}
