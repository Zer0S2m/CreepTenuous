package com.zer0s2m.creeptenuous.core.atomic;

import com.zer0s2m.creeptenuous.core.atomic.handlers.AtomicSystemCallManager;
import com.zer0s2m.creeptenuous.core.atomic.mock.MockServiceFileSystem;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

@Tag("core")
public class AtomicSystemCallManagerTests {

    @Nested
    class AtomicSystemCallManagerTestsOperationCreate {

        @RepeatedTest(10)
        public void createFileSuccess() throws IOException {
            final String title = UUID.randomUUID().toString();
            Path path = Path.of(System.getProperty("java.io.tmpdir"), title);

            Path source = Assertions.assertDoesNotThrow(
                    () -> AtomicSystemCallManager.call(
                            new MockServiceFileSystem.MockServiceFileSystemCreateFileSuccess(),
                            path
                    )
            );

            Assertions.assertTrue(Files.exists(source));
            Files.deleteIfExists(source);
        }

        @RepeatedTest(10)
        public void createDirectorySuccess() throws IOException {
            final String title = UUID.randomUUID().toString();
            Path path = Path.of(System.getProperty("java.io.tmpdir"), title);

            Path source = Assertions.assertDoesNotThrow(
                    () -> AtomicSystemCallManager.call(
                            new MockServiceFileSystem.MockServiceFileSystemCreateDirectorySuccess(),
                            path
                    )
            );

            Assertions.assertTrue(Files.exists(source));
            Files.deleteIfExists(source);
        }

        @RepeatedTest(10)
        public void createFileFail_directlyThrownExactException_handlerAtomicSuccess() {
            final String title = UUID.randomUUID().toString();
            Path path = Path.of(System.getProperty("java.io.tmpdir"), title);

            assertThatExceptionOfType(InvocationTargetException.class)
                    .isThrownBy(() -> AtomicSystemCallManager.call(
                            new MockServiceFileSystem.MockServiceFileSystemCreateFileFailExactException(),
                            path
                    ))
                    .withRootCauseInstanceOf(IOException.class);

            Assertions.assertFalse(Files.exists(path));
        }

        @RepeatedTest(10)
        public void createDirectoryFail_directoryThrownExactException_handlerAtomicSuccess() {
            final String title = UUID.randomUUID().toString();
            Path path = Path.of(System.getProperty("java.io.tmpdir"), title);

            assertThatExceptionOfType(InvocationTargetException.class)
                    .isThrownBy(() -> AtomicSystemCallManager.call(
                            new MockServiceFileSystem.MockServiceFileSystemCreateDirectoryFailExactException(),
                            path
                    ))
                    .withRootCauseInstanceOf(IOException.class);

            Assertions.assertFalse(Files.exists(path));
        }

        @RepeatedTest(10)
        public void createFileFail_directlyThrownOtherException_handlerAtomicFail() throws IOException {
            final String title = UUID.randomUUID().toString();
            Path path = Path.of(System.getProperty("java.io.tmpdir"), title);

            assertThatExceptionOfType(InvocationTargetException.class)
                    .isThrownBy(() -> AtomicSystemCallManager.call(
                            new MockServiceFileSystem.MockServiceFileSystemCreateFileFailOtherException(),
                            path
                    ))
                    .withRootCauseInstanceOf(Exception.class);

            Assertions.assertTrue(Files.exists(path));
            Files.deleteIfExists(path);
        }

        @RepeatedTest(10)
        public void createDirectoryFail_directoryThrownOtherException_handlerAtomicFail() throws IOException {
            final String title = UUID.randomUUID().toString();
            Path path = Path.of(System.getProperty("java.io.tmpdir"), title);

            assertThatExceptionOfType(InvocationTargetException.class)
                    .isThrownBy(() -> AtomicSystemCallManager.call(
                            new MockServiceFileSystem.MockServiceFileSystemCreateDirectoryFailOtherException(),
                            path
                    ))
                    .withRootCauseInstanceOf(Exception.class);

            Assertions.assertTrue(Files.exists(path));
            Files.deleteIfExists(path);
        }

        @RepeatedTest(10)
        public void createFileFail_directlyThrownMultiException_handlerAtomicFail() {
            final String title = UUID.randomUUID().toString();
            Path path = Path.of(System.getProperty("java.io.tmpdir"), title);

            assertThatExceptionOfType(InvocationTargetException.class)
                    .isThrownBy(() -> AtomicSystemCallManager.call(
                            new MockServiceFileSystem.MockServiceFileSystemCreateFileFailMultiException(),
                            path
                    ))
                    .withRootCauseInstanceOf(Exception.class);

            Assertions.assertFalse(Files.exists(path));
        }

        @RepeatedTest(10)
        public void createDirectoryFail_directoryThrownMultiException_handlerAtomicFail() {
            final String title = UUID.randomUUID().toString();
            Path path = Path.of(System.getProperty("java.io.tmpdir"), title);

            assertThatExceptionOfType(InvocationTargetException.class)
                    .isThrownBy(() -> AtomicSystemCallManager.call(
                            new MockServiceFileSystem.MockServiceFileSystemCreateDirectoryFailMultiException(),
                            path
                    ))
                    .withRootCauseInstanceOf(Exception.class);

            Assertions.assertFalse(Files.exists(path));
        }

        @RepeatedTest(10)
        public void createFileFail_directlyThrownOtherMethodException_handlerAtomicFail() {
            final MockServiceFileSystem.MockServiceFileSystemCreateFileFailOtherMethodException
                    service = new MockServiceFileSystem.MockServiceFileSystemCreateFileFailOtherMethodException();

            final String title = UUID.randomUUID().toString();
            Path path = Path.of(System.getProperty("java.io.tmpdir"), title);

            assertThatExceptionOfType(InvocationTargetException.class)
                    .isThrownBy(() -> AtomicSystemCallManager.call(
                            service,
                            path
                    ))
                    .withRootCauseInstanceOf(Exception.class);

            Assertions.assertFalse(Files.exists(path));
        }

        @RepeatedTest(10)
        public void createDirectoryFail_directoryThrownOtherMethodException_handlerAtomicFail() {
            final MockServiceFileSystem.MockServiceFileSystemCreateDirectoryFailOtherMethodException
                    service = new MockServiceFileSystem.MockServiceFileSystemCreateDirectoryFailOtherMethodException();

            final String title = UUID.randomUUID().toString();
            Path path = Path.of(System.getProperty("java.io.tmpdir"), title);

            assertThatExceptionOfType(InvocationTargetException.class)
                    .isThrownBy(() -> AtomicSystemCallManager.call(
                            service,
                            path
                    ))
                    .withRootCauseInstanceOf(Exception.class);

            Assertions.assertFalse(Files.exists(path));
        }

    }

}
