package com.zer0s2m.creeptenuous.core.atomic.mock;

import com.zer0s2m.creeptenuous.core.atomic.annotations.AtomicFileSystem;
import com.zer0s2m.creeptenuous.core.atomic.annotations.AtomicFileSystemExceptionHandler;
import com.zer0s2m.creeptenuous.core.atomic.annotations.CoreServiceFileSystem;
import com.zer0s2m.creeptenuous.core.atomic.context.ContextAtomicFileSystem;
import com.zer0s2m.creeptenuous.core.atomic.context.nio.file.FilesContextAtomic;
import com.zer0s2m.creeptenuous.core.atomic.handlers.impl.ServiceFileSystemExceptionHandlerOperationCreate;
import com.zer0s2m.creeptenuous.core.atomic.handlers.impl.ServiceFileSystemExceptionHandlerOperationDelete;
import com.zer0s2m.creeptenuous.core.atomic.handlers.impl.ServiceFileSystemExceptionHandlerOperationUpload;
import com.zer0s2m.creeptenuous.core.atomic.services.AtomicServiceFileSystem;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;

/**
 * Test class for testing atomic mode in different positions.
 * <p>Supports the following types of operations:</p>
 * <ul>
 *     <li>{@link ContextAtomicFileSystem.Operations#DELETE}</li>
 *     <li>{@link ContextAtomicFileSystem.Operations#CREATE}</li>
 *     <li>{@link ContextAtomicFileSystem.Operations#UPLOAD}</li>
 *     <li>{@link ContextAtomicFileSystem.Operations#DOWNLOAD}</li>
 *     <li>{@link ContextAtomicFileSystem.Operations#COPY}</li>
 *     <li>{@link ContextAtomicFileSystem.Operations#MOVE}</li>
 *     <li>{@link ContextAtomicFileSystem.Operations#FRAGMENTATION}</li>
 * </ul>
 */
@SuppressWarnings("unused")
public final class MockServiceFileSystem {

    /**
     * <p>------------------------------------------------</p>
     * {@link ContextAtomicFileSystem.Operations#DELETE}
     */

    @CoreServiceFileSystem(method = "delete")
    public static final class MockServiceFileSystemDeleteFileSuccess implements AtomicServiceFileSystem {

        @AtomicFileSystem(
                name = "delete-file",
                handlers = {
                        @AtomicFileSystemExceptionHandler(
                                exception = IOException.class,
                                handler = ServiceFileSystemExceptionHandlerOperationDelete.class,
                                operation = ContextAtomicFileSystem.Operations.DELETE
                        )
                }
        )
        public void delete(Path source) throws IOException {
            FilesContextAtomic.delete(source);
        }

    }

    @CoreServiceFileSystem(method = "delete")
    public static final class MockServiceFileSystemDeleteDirectorySuccess implements AtomicServiceFileSystem {

        @AtomicFileSystem(
                name = "delete-directory",
                handlers = {
                        @AtomicFileSystemExceptionHandler(
                                exception = IOException.class,
                                handler = ServiceFileSystemExceptionHandlerOperationDelete.class,
                                operation = ContextAtomicFileSystem.Operations.DELETE
                        )
                }
        )
        public void delete(Path source) throws IOException {
            FilesContextAtomic.delete(source);
        }

    }

    @CoreServiceFileSystem(method = "delete")
    public static final class MockServiceFileSystemDeleteFileFailException implements AtomicServiceFileSystem {

        @AtomicFileSystem(
                name = "delete-file",
                handlers = {
                        @AtomicFileSystemExceptionHandler(
                                exception = IOException.class,
                                handler = ServiceFileSystemExceptionHandlerOperationDelete.class,
                                operation = ContextAtomicFileSystem.Operations.DELETE
                        )
                }
        )
        public void delete(Path source) throws IOException {
            FilesContextAtomic.delete(source);
            throw new IOException(source.toString());
        }

    }

    @CoreServiceFileSystem(method = "delete")
    public static final class MockServiceFileSystemDeleteDirectoryFailException implements AtomicServiceFileSystem {

        @AtomicFileSystem(
                name = "delete-directory",
                handlers = {
                        @AtomicFileSystemExceptionHandler(
                                exception = IOException.class,
                                handler = ServiceFileSystemExceptionHandlerOperationDelete.class,
                                operation = ContextAtomicFileSystem.Operations.DELETE
                        )
                }
        )
        public void delete(Path source) throws IOException {
            FilesContextAtomic.delete(source);
            throw new IOException(source.toString());
        }

    }

    @CoreServiceFileSystem(method = "delete")
    public static final class MockServiceFileSystemDeleteFileFailOtherException implements AtomicServiceFileSystem {

        @AtomicFileSystem(
                name = "delete-file",
                handlers = {
                        @AtomicFileSystemExceptionHandler(
                                exception = IOException.class,
                                handler = ServiceFileSystemExceptionHandlerOperationDelete.class,
                                operation = ContextAtomicFileSystem.Operations.DELETE
                        )
                }
        )
        public void delete(Path source) throws Exception {
            FilesContextAtomic.delete(source);
            throw new Exception(source.toString());
        }

    }

    @CoreServiceFileSystem(method = "delete")
    public static final class MockServiceFileSystemDeleteDirectoryFailOtherException implements AtomicServiceFileSystem {

        @AtomicFileSystem(
                name = "delete-directory",
                handlers = {
                        @AtomicFileSystemExceptionHandler(
                                exception = IOException.class,
                                handler = ServiceFileSystemExceptionHandlerOperationDelete.class,
                                operation = ContextAtomicFileSystem.Operations.DELETE
                        )
                }
        )
        public void delete(Path source) throws Exception {
            FilesContextAtomic.delete(source);
            throw new Exception(source.toString());
        }

    }

    @CoreServiceFileSystem(method = "delete")
    public static final class MockServiceFileSystemDeleteFileFailMultiException implements AtomicServiceFileSystem {

        @AtomicFileSystem(
                name = "delete-file",
                handlers = {
                        @AtomicFileSystemExceptionHandler(
                                isExceptionMulti = true,
                                handler = ServiceFileSystemExceptionHandlerOperationDelete.class,
                                operation = ContextAtomicFileSystem.Operations.DELETE
                        )
                }
        )
        public void delete(Path source) throws IOException {
            FilesContextAtomic.delete(source);
            throw new IOException(source.toString());
        }

    }

    @CoreServiceFileSystem(method = "delete")
    public static final class MockServiceFileSystemDeleteDirectoryFailMultiException implements AtomicServiceFileSystem {

        @AtomicFileSystem(
                name = "delete-directory",
                handlers = {
                        @AtomicFileSystemExceptionHandler(
                                isExceptionMulti = true,
                                handler = ServiceFileSystemExceptionHandlerOperationDelete.class,
                                operation = ContextAtomicFileSystem.Operations.DELETE
                        )
                }
        )
        public void delete(Path source) throws IOException {
            FilesContextAtomic.delete(source);
            throw new IOException(source.toString());
        }

    }

    @CoreServiceFileSystem(method = "delete")
    public static final class MockServiceFileSystemDeleteFileFailOtherMethodException implements AtomicServiceFileSystem {

        @AtomicFileSystem(
                name = "delete-file",
                handlers = {
                        @AtomicFileSystemExceptionHandler(
                                exception = IOException.class,
                                handler = ServiceFileSystemExceptionHandlerOperationDelete.class,
                                operation = ContextAtomicFileSystem.Operations.DELETE
                        )
                }
        )
        public void delete(Path source) throws IOException {
            FilesContextAtomic.delete(source);
            delete_(source);
        }

        @Contract("_ -> fail")
        private void delete_(@NotNull Path source) throws IOException {
            throw new IOException(source.toString());
        }

    }

    @CoreServiceFileSystem(method = "delete")
    public static final class MockServiceFileSystemDeleteDirectoryFailOtherMethodException implements AtomicServiceFileSystem {

        @AtomicFileSystem(
                name = "delete-directory",
                handlers = {
                        @AtomicFileSystemExceptionHandler(
                                exception = IOException.class,
                                handler = ServiceFileSystemExceptionHandlerOperationDelete.class,
                                operation = ContextAtomicFileSystem.Operations.DELETE
                        )
                }
        )
        public void delete(Path source) throws IOException {
            FilesContextAtomic.delete(source);
            delete_(source);
        }

        @Contract("_ -> fail")
        private void delete_(@NotNull Path source) throws IOException {
            throw new IOException(source.toString());
        }

    }

    /**
     * <p>------------------------------------------------</p>
     * {@link ContextAtomicFileSystem.Operations#CREATE}
     */

    @CoreServiceFileSystem(method = "create")
    public static final class MockServiceFileSystemCreateFileSuccess implements AtomicServiceFileSystem {

        @AtomicFileSystem(
                name = "create-file",
                handlers = {
                        @AtomicFileSystemExceptionHandler(
                                exception = IOException.class,
                                handler = ServiceFileSystemExceptionHandlerOperationCreate.class,
                                operation = ContextAtomicFileSystem.Operations.CREATE
                        )
                }
        )
        public Path create(Path path) throws IOException {
            return FilesContextAtomic.createFile(path);
        }

    }

    @CoreServiceFileSystem(method = "create")
    public static final class MockServiceFileSystemCreateDirectorySuccess implements AtomicServiceFileSystem {

        @AtomicFileSystem(
                name = "create-directory",
                handlers = {
                        @AtomicFileSystemExceptionHandler(
                                exception = IOException.class,
                                handler = ServiceFileSystemExceptionHandlerOperationCreate.class,
                                operation = ContextAtomicFileSystem.Operations.CREATE
                        )
                }
        )
        public Path create(Path path) throws IOException {
            return FilesContextAtomic.createDirectory(path);
        }

    }

    @CoreServiceFileSystem(method = "create")
    public static final class MockServiceFileSystemCreateFileFailExactException
            implements AtomicServiceFileSystem {

        @AtomicFileSystem(
                name = "create-file",
                handlers = {
                        @AtomicFileSystemExceptionHandler(
                                exception = IOException.class,
                                handler = ServiceFileSystemExceptionHandlerOperationCreate.class,
                                operation = ContextAtomicFileSystem.Operations.CREATE
                        )
                }
        )
        public Path create(Path path) throws IOException {
            Path source = FilesContextAtomic.createFile(path);
            throw new IOException(source.toString());
        }

    }

    @CoreServiceFileSystem(method = "create")
    public static final class MockServiceFileSystemCreateDirectoryFailExactException
            implements AtomicServiceFileSystem {

        @AtomicFileSystem(
                name = "create-directory",
                handlers = {
                        @AtomicFileSystemExceptionHandler(
                                exception = IOException.class,
                                handler = ServiceFileSystemExceptionHandlerOperationCreate.class,
                                operation = ContextAtomicFileSystem.Operations.CREATE
                        )
                }
        )
        public Path create(Path path) throws IOException {
            Path source = FilesContextAtomic.createDirectory(path);
            throw new IOException(source.toString());
        }

    }

    @CoreServiceFileSystem(method = "create")
    public static final class MockServiceFileSystemCreateFileFailOtherException
            implements AtomicServiceFileSystem {

        @AtomicFileSystem(
                name = "create-file",
                handlers = {
                        @AtomicFileSystemExceptionHandler(
                                exception = IOException.class,
                                handler = ServiceFileSystemExceptionHandlerOperationCreate.class,
                                operation = ContextAtomicFileSystem.Operations.CREATE
                        )
                }
        )
        public Path create(Path path) throws Exception {
            Path source = FilesContextAtomic.createFile(path);
            throw new Exception(source.toString());
        }

    }

    @CoreServiceFileSystem(method = "create")
    public static final class MockServiceFileSystemCreateDirectoryFailOtherException
            implements AtomicServiceFileSystem {

        @AtomicFileSystem(
                name = "create-directory",
                handlers = {
                        @AtomicFileSystemExceptionHandler(
                                exception = IOException.class,
                                handler = ServiceFileSystemExceptionHandlerOperationCreate.class,
                                operation = ContextAtomicFileSystem.Operations.CREATE
                        )
                }
        )
        public Path create(Path path) throws Exception {
            Path source = FilesContextAtomic.createDirectory(path);
            throw new Exception(source.toString());
        }

    }

    @CoreServiceFileSystem(method = "create")
    public static final class MockServiceFileSystemCreateFileFailMultiException
            implements AtomicServiceFileSystem {

        @AtomicFileSystem(
                name = "create-file",
                handlers = {
                        @AtomicFileSystemExceptionHandler(
                                isExceptionMulti = true,
                                handler = ServiceFileSystemExceptionHandlerOperationCreate.class,
                                operation = ContextAtomicFileSystem.Operations.CREATE
                        )
                }
        )
        public Path create(Path path) throws IOException {
            Path source = FilesContextAtomic.createFile(path);
            throw new IOException(source.toString());
        }

    }

    @CoreServiceFileSystem(method = "create")
    public static final class MockServiceFileSystemCreateDirectoryFailMultiException
            implements AtomicServiceFileSystem {

        @AtomicFileSystem(
                name = "create-directory",
                handlers = {
                        @AtomicFileSystemExceptionHandler(
                                isExceptionMulti = true,
                                handler = ServiceFileSystemExceptionHandlerOperationCreate.class,
                                operation = ContextAtomicFileSystem.Operations.CREATE
                        )
                }
        )
        public Path create(Path path) throws IOException {
            Path source = FilesContextAtomic.createDirectory(path);
            throw new IOException(source.toString());
        }

    }

    @CoreServiceFileSystem(method = "create")
    public static final class MockServiceFileSystemCreateFileFailOtherMethodException
            implements AtomicServiceFileSystem {

        @AtomicFileSystem(
                name = "create-file",
                handlers = {
                        @AtomicFileSystemExceptionHandler(
                                exception = IOException.class,
                                handler = ServiceFileSystemExceptionHandlerOperationCreate.class,
                                operation = ContextAtomicFileSystem.Operations.CREATE
                        )
                }
        )
        public Path create(Path path) throws IOException {
            Path source = FilesContextAtomic.createFile(path);
            return create_(source);
        }

        @Contract("_ -> fail")
        private Path create_(@NotNull Path source) throws IOException {
            throw new IOException(source.toString());
        }

    }

    @CoreServiceFileSystem(method = "create")
    public static final class MockServiceFileSystemCreateDirectoryFailOtherMethodException
            implements AtomicServiceFileSystem {

        @AtomicFileSystem(
                name = "create-directory",
                handlers = {
                        @AtomicFileSystemExceptionHandler(
                                exception = IOException.class,
                                handler = ServiceFileSystemExceptionHandlerOperationCreate.class,
                                operation = ContextAtomicFileSystem.Operations.CREATE
                        )
                }
        )
        public Path create(Path path) throws IOException {
            Path source = FilesContextAtomic.createDirectory(path);
            return create_(source);
        }

        @Contract("_ -> fail")
        private Path create_(@NotNull Path source) throws IOException {
            throw new IOException(source.toString());
        }

    }

    /**
     * <p>------------------------------------------------</p>
     * {@link ContextAtomicFileSystem.Operations#UPLOAD}
     */

    @CoreServiceFileSystem(method = "upload")
    public static final class MockServiceFileSystemUploadFileSuccess implements AtomicServiceFileSystem {

        @AtomicFileSystem(
                name = "upload-file",
                handlers = {
                        @AtomicFileSystemExceptionHandler(
                                exception = IOException.class,
                                handler = ServiceFileSystemExceptionHandlerOperationUpload.class,
                                operation = ContextAtomicFileSystem.Operations.UPLOAD
                        )
                }
        )
        public long upload(FileInputStream source, Path target) throws IOException {
            return FilesContextAtomic.copy(source, target);
        }

    }

    @CoreServiceFileSystem(method = "upload")
    public static final class MockServiceFileSystemUploadDirectorySuccess implements AtomicServiceFileSystem {

        @AtomicFileSystem(
                name = "upload-directory",
                handlers = {
                        @AtomicFileSystemExceptionHandler(
                                exception = IOException.class,
                                handler = ServiceFileSystemExceptionHandlerOperationUpload.class,
                                operation = ContextAtomicFileSystem.Operations.UPLOAD
                        )
                }
        )
        public Path upload(Path source, Path target) throws IOException {
            return FilesContextAtomic.upload(source, target);
        }

    }

    @CoreServiceFileSystem(method = "upload")
    public static final class MockServiceFileSystemUploadFileFailException implements AtomicServiceFileSystem {

        @AtomicFileSystem(
                name = "upload-file",
                handlers = {
                        @AtomicFileSystemExceptionHandler(
                                exception = IOException.class,
                                handler = ServiceFileSystemExceptionHandlerOperationUpload.class,
                                operation = ContextAtomicFileSystem.Operations.UPLOAD
                        )
                }
        )
        public long upload(FileInputStream source, Path target) throws IOException {
            FilesContextAtomic.copy(source, target);
            throw new IOException(source.toString());
        }

    }

    @CoreServiceFileSystem(method = "upload")
    public static final class MockServiceFileSystemUploadDirectoryFailException implements AtomicServiceFileSystem {

        @AtomicFileSystem(
                name = "upload-directory",
                handlers = {
                        @AtomicFileSystemExceptionHandler(
                                exception = IOException.class,
                                handler = ServiceFileSystemExceptionHandlerOperationUpload.class,
                                operation = ContextAtomicFileSystem.Operations.UPLOAD
                        )
                }
        )
        public Path upload(Path source, Path target) throws IOException {
            FilesContextAtomic.upload(source, target);
            throw new IOException(source.toString());
        }

    }

    @CoreServiceFileSystem(method = "upload")
    public static final class MockServiceFileSystemUploadFileFailOtherException implements AtomicServiceFileSystem {

        @AtomicFileSystem(
                name = "upload-file",
                handlers = {
                        @AtomicFileSystemExceptionHandler(
                                exception = IOException.class,
                                handler = ServiceFileSystemExceptionHandlerOperationUpload.class,
                                operation = ContextAtomicFileSystem.Operations.UPLOAD
                        )
                }
        )
        public long upload(FileInputStream source, Path target) throws Exception {
            FilesContextAtomic.copy(source, target);
            throw new Exception(source.toString());
        }

    }

    @CoreServiceFileSystem(method = "upload")
    public static final class MockServiceFileSystemUploadDirectoryFailOtherException implements AtomicServiceFileSystem {

        @AtomicFileSystem(
                name = "upload-directory",
                handlers = {
                        @AtomicFileSystemExceptionHandler(
                                exception = IOException.class,
                                handler = ServiceFileSystemExceptionHandlerOperationUpload.class,
                                operation = ContextAtomicFileSystem.Operations.UPLOAD
                        )
                }
        )
        public Path upload(Path source, Path target) throws Exception {
            FilesContextAtomic.upload(source, target);
            throw new Exception(source.toString());
        }

    }

    @CoreServiceFileSystem(method = "upload")
    public static final class MockServiceFileSystemUploadFileFailMultiException implements AtomicServiceFileSystem {

        @AtomicFileSystem(
                name = "upload-file",
                handlers = {
                        @AtomicFileSystemExceptionHandler(
                                isExceptionMulti = true,
                                handler = ServiceFileSystemExceptionHandlerOperationUpload.class,
                                operation = ContextAtomicFileSystem.Operations.UPLOAD
                        )
                }
        )
        public long upload(FileInputStream source, Path target) throws Exception {
            FilesContextAtomic.copy(source, target);
            throw new Exception(source.toString());
        }

    }

    @CoreServiceFileSystem(method = "upload")
    public static final class MockServiceFileSystemUploadDirectoryFailMultiException implements AtomicServiceFileSystem {

        @AtomicFileSystem(
                name = "upload-directory",
                handlers = {
                        @AtomicFileSystemExceptionHandler(
                                isExceptionMulti = true,
                                handler = ServiceFileSystemExceptionHandlerOperationUpload.class,
                                operation = ContextAtomicFileSystem.Operations.UPLOAD
                        )
                }
        )
        public Path upload(Path source, Path target) throws Exception {
            FilesContextAtomic.upload(source, target);
            throw new Exception(source.toString());
        }

    }

    @CoreServiceFileSystem(method = "upload")
    public static final class MockServiceFileSystemUploadFileFailOtherMethodException
            implements AtomicServiceFileSystem {

        @AtomicFileSystem(
                name = "upload-file",
                handlers = {
                        @AtomicFileSystemExceptionHandler(
                                exception = IOException.class,
                                handler = ServiceFileSystemExceptionHandlerOperationUpload.class,
                                operation = ContextAtomicFileSystem.Operations.UPLOAD
                        )
                }
        )
        public long upload(FileInputStream source, Path target) throws IOException {
            FilesContextAtomic.copy(source, target);
            return upload(source);
        }

        @Contract("_ -> fail")
        private long upload(@NotNull FileInputStream source) throws IOException {
            throw new IOException(source.toString());
        }

    }

    @CoreServiceFileSystem(method = "upload")
    public static final class MockServiceFileSystemUploadDirectoryFailOtherMethodException
            implements AtomicServiceFileSystem {

        @AtomicFileSystem(
                name = "upload-directory",
                handlers = {
                        @AtomicFileSystemExceptionHandler(
                                exception = IOException.class,
                                handler = ServiceFileSystemExceptionHandlerOperationUpload.class,
                                operation = ContextAtomicFileSystem.Operations.UPLOAD
                        )
                }
        )
        public Path upload(Path source, Path target) throws IOException {
            FilesContextAtomic.upload(source, target);
            return upload(source);
        }

        @Contract("_ -> fail")
        private Path upload(@NotNull Path source) throws IOException {
            throw new IOException(source.toString());
        }

    }

}
