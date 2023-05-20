package com.zer0s2m.creeptenuous.core.context;

import com.zer0s2m.creeptenuous.core.annotations.AtomicFileSystemExceptionHandler;
import com.zer0s2m.creeptenuous.core.handlers.ServiceFileSystemExceptionHandler;
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

    /**
     * <p> File system interaction data</p>
     * <p>
     *     Basic key names and value types:<br>
     *     <ul>
     *         <li><b>operation</b> - {@link Operations}</li>
     *         <li><b>realName</b> - {@link String}</li>
     *         <li><b>systemName</b> - {@link String}</li>
     *         <li><b>realPath</b> - {@link java.nio.file.Path}</li>
     *         <li><b>systemPath</b> - {@link java.nio.file.Path}</li>
     *     </ul>
     * </p>
     * Example:
     * <pre>{@code
     * HashMap<String, Object> operationData = new HashMap<>();
     * operationData.put("realName", realNameFile);
     * operationData.put("systemName", systemNameFile);
     * operationData.put("realPath", Path.of(uri));
     * operationData.put("systemPath", Path.of(uri));
     * contextAtomicFileSystem.addOperationData("unique-name", operationData);
     * }</pre>
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
        DOWNLOAD
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
    public void addOperationData(String key, HashMap<String, Object> value) {
        this.operationsData.put(key, value);
    }

    /**
     * Get operation data
     * @param key unique file system object name (uuid
     * @return operation custom data (copied) {@link #operationsData}
     */
    public HashMap<String, Object> getOperationData(String key) {
        return new HashMap<>(operationsData.get(key));
    }

    /**
     * Get data
     * @return operation custom data (copied) {@link #operationsData}
     */
    public HashMap<String, HashMap<String, Object>> getOperationsData() {
        return new HashMap<>(operationsData);
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
    private void writeOperationDataLogger(Operations operation, String objectOperationData) {
        final String baseLog = "Run operation [%s] context atomic mode file system object [%s]";
        if (operation.equals(Operations.CREATE)) {
            logger.info(String.format(baseLog, "create", objectOperationData));
        } else if (operation.equals(Operations.DELETE)) {
            logger.info(String.format(baseLog, "delete", objectOperationData));
        } else if (operation.equals(Operations.UPLOAD)) {
            logger.info(String.format(baseLog, "upload", objectOperationData));
        } else if (operation.equals(Operations.DOWNLOAD)) {
            logger.info(String.format(baseLog, "download", objectOperationData));
        }
    }

    /**
     * Clearing file system object data
     * @param key object data file system (key)
     */
    private void cleanOperationData(String key) {
        this.operationsData.remove(key);
    }
}
