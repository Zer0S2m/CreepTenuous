package com.zer0s2m.creeptenuous.core.atomic.mock;

import com.zer0s2m.creeptenuous.core.atomic.annotations.AtomicFileSystem;
import com.zer0s2m.creeptenuous.core.atomic.annotations.AtomicFileSystemExceptionHandler;
import com.zer0s2m.creeptenuous.core.atomic.annotations.CoreServiceFileSystem;
import com.zer0s2m.creeptenuous.core.atomic.context.ContextAtomicFileSystem;
import com.zer0s2m.creeptenuous.core.atomic.context.nio.file.FilesContextAtomic;
import com.zer0s2m.creeptenuous.core.atomic.handlers.impl.ServiceFileSystemExceptionHandlerOperationCreate;
import com.zer0s2m.creeptenuous.core.atomic.services.AtomicServiceFileSystem;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

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
public final class MockServiceFileSystem {

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

}
