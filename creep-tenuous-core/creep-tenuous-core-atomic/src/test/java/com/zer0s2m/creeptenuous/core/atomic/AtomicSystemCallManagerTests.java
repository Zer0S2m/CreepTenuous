package com.zer0s2m.creeptenuous.core.atomic;

import com.zer0s2m.creeptenuous.core.atomic.handlers.AtomicSystemCallManager;
import com.zer0s2m.creeptenuous.core.atomic.mock.MockServiceFileSystem;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.UUID;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

@Tag("core")
public class AtomicSystemCallManagerTests {

    private final String testTitleDirectory = UUID.randomUUID().toString();

    private @NotNull Path tmpDirectory() throws IOException {
        Path path = Path.of(System.getProperty("java.io.tmpdir"), testTitleDirectory);
        if (!Files.exists(path)) {
            Files.createDirectory(path);
        }
        return path;
    }

    private void deleteDirectory(Path source) throws IOException {
        try (Stream<Path> stream = Files.walk(source)) {
            stream
                    .sorted(Comparator.reverseOrder())
                    .map(Path::toFile)
                    .forEach(File::delete);
        }
    }

    @Nested
    class AtomicSystemCallManagerTestsOperationDelete {

        @RepeatedTest(10)
        public void deleteFileSuccess() throws IOException {
            final String title = UUID.randomUUID() + ".txt";
            Path path = Path.of(tmpDirectory().toString(), title);
            Files.createFile(path);

            Assertions.assertDoesNotThrow(
                    () -> AtomicSystemCallManager.call(
                            new MockServiceFileSystem.MockServiceFileSystemDeleteFileSuccess(),
                            path
                    )
            );

            Assertions.assertFalse(Files.exists(path));
        }

        @RepeatedTest(10)
        public void deleteDirectorySuccess() throws IOException {
            final String title = UUID.randomUUID().toString();
            Path path = Path.of(tmpDirectory().toString(), title);
            Files.createDirectory(path);

            Assertions.assertDoesNotThrow(
                    () -> AtomicSystemCallManager.call(
                            new MockServiceFileSystem.MockServiceFileSystemDeleteDirectorySuccess(),
                            path
                    )
            );

            Assertions.assertFalse(Files.exists(path));
        }

        @RepeatedTest(10)
        public void deleteFileFail_directlyThrownExactException_handlerAtomicSuccess() throws IOException {
            final String title = UUID.randomUUID() + ".txt";
            Path path = Path.of(tmpDirectory().toString(), title);
            Files.createFile(path);

            assertThatExceptionOfType(InvocationTargetException.class)
                    .isThrownBy(() -> AtomicSystemCallManager.call(
                            new MockServiceFileSystem.MockServiceFileSystemDeleteFileFailException(),
                            path
                    ))
                    .withRootCauseInstanceOf(IOException.class);

            Assertions.assertTrue(Files.exists(path));
            Files.delete(path);
        }

        @RepeatedTest(10)
        public void deleteDirectoryFail_directlyThrownExactException_handlerAtomicSuccess() throws IOException {
            final String title = UUID.randomUUID().toString();
            Path path = Path.of(tmpDirectory().toString(), title);
            Files.createDirectory(path);

            assertThatExceptionOfType(InvocationTargetException.class)
                    .isThrownBy(() -> AtomicSystemCallManager.call(
                            new MockServiceFileSystem.MockServiceFileSystemDeleteDirectoryFailException(),
                            path
                    ))
                    .withRootCauseInstanceOf(IOException.class);

            Assertions.assertTrue(Files.exists(path));
            Files.delete(path);
        }

        @RepeatedTest(10)
        public void deleteFileFail_directlyThrownOtherException_handlerAtomicFail() throws IOException {
            final String title = UUID.randomUUID() + ".txt";
            Path path = Path.of(tmpDirectory().toString(), title);
            Files.createFile(path);

            assertThatExceptionOfType(InvocationTargetException.class)
                    .isThrownBy(() -> AtomicSystemCallManager.call(
                            new MockServiceFileSystem.MockServiceFileSystemDeleteFileFailOtherException(),
                            path
                    ))
                    .withRootCauseInstanceOf(Exception.class);

            Assertions.assertFalse(Files.exists(path));
        }

        @RepeatedTest(10)
        public void deleteDirectoryFail_directlyThrownOtherException_handlerAtomicFail() throws IOException {
            final String title = UUID.randomUUID().toString();
            Path path = Path.of(tmpDirectory().toString(), title);
            Files.createDirectory(path);

            assertThatExceptionOfType(InvocationTargetException.class)
                    .isThrownBy(() -> AtomicSystemCallManager.call(
                            new MockServiceFileSystem.MockServiceFileSystemDeleteDirectoryFailOtherException(),
                            path
                    ))
                    .withRootCauseInstanceOf(Exception.class);

            Assertions.assertFalse(Files.exists(path));
        }

        @RepeatedTest(10)
        public void deleteFileFail_directlyThrownMultiException_handlerAtomicSuccess() throws IOException {
            final String title = UUID.randomUUID() + ".txt";
            Path path = Path.of(tmpDirectory().toString(), title);
            Files.createFile(path);

            assertThatExceptionOfType(InvocationTargetException.class)
                    .isThrownBy(() -> AtomicSystemCallManager.call(
                            new MockServiceFileSystem.MockServiceFileSystemDeleteFileFailMultiException(),
                            path
                    ))
                    .withRootCauseInstanceOf(IOException.class);

            Assertions.assertTrue(Files.exists(path));
            Files.delete(path);
        }

        @RepeatedTest(10)
        public void deleteDirectoryFail_directlyThrownMultiException_handlerAtomicSuccess() throws IOException {
            final String title = UUID.randomUUID().toString();
            Path path = Path.of(tmpDirectory().toString(), title);
            Files.createDirectory(path);

            assertThatExceptionOfType(InvocationTargetException.class)
                    .isThrownBy(() -> AtomicSystemCallManager.call(
                            new MockServiceFileSystem.MockServiceFileSystemDeleteDirectoryFailMultiException(),
                            path
                    ))
                    .withRootCauseInstanceOf(IOException.class);

            Assertions.assertTrue(Files.exists(path));
            Files.delete(path);
        }

        @RepeatedTest(10)
        public void deleteFileFail_directoryThrownOtherMethodException_handlerAtomicSuccess() throws IOException {
            final String title = UUID.randomUUID() + ".txt";
            Path path = Path.of(tmpDirectory().toString(), title);
            Files.createFile(path);

            assertThatExceptionOfType(InvocationTargetException.class)
                    .isThrownBy(() -> AtomicSystemCallManager.call(
                            new MockServiceFileSystem.MockServiceFileSystemDeleteFileFailOtherMethodException(),
                            path
                    ))
                    .withRootCauseInstanceOf(IOException.class);

            Assertions.assertTrue(Files.exists(path));
            Files.delete(path);
        }

        @RepeatedTest(10)
        public void deleteDirectoryFail_directoryThrownOtherMethodException_handlerAtomicSuccess() throws IOException {
            final String title = UUID.randomUUID().toString();
            Path path = Path.of(tmpDirectory().toString(), title);
            Files.createDirectory(path);

            assertThatExceptionOfType(InvocationTargetException.class)
                    .isThrownBy(() -> AtomicSystemCallManager.call(
                            new MockServiceFileSystem.MockServiceFileSystemDeleteDirectoryFailOtherMethodException(),
                            path
                    ))
                    .withRootCauseInstanceOf(IOException.class);

            Assertions.assertTrue(Files.exists(path));
            Files.delete(path);
        }

    }

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
            Files.delete(source);
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
            Files.delete(source);
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
            Files.delete(path);
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
            Files.delete(path);
        }

        @RepeatedTest(10)
        public void createFileFail_directlyThrownMultiException_handlerAtomicSuccess() {
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
        public void createDirectoryFail_directoryThrownMultiException_handlerAtomicSuccess() {
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
        public void createFileFail_directlyThrownOtherMethodException_handlerAtomicSuccess() {
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
        public void createDirectoryFail_directoryThrownOtherMethodException_handlerAtomicSuccess() {
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

    @Nested
    class AtomicSystemCallManagerTestsOperationUpload {

        @RepeatedTest(10)
        public void uploadFileSuccess() throws IOException {
            final String title = UUID.randomUUID() + ".txt";
            Path source = Path.of(System.getProperty("java.io.tmpdir"), title);
            Path target = Path.of(tmpDirectory().toString(), title);
            Files.createFile(source);

            Assertions.assertDoesNotThrow(
                    () -> AtomicSystemCallManager.call(
                            new MockServiceFileSystem.MockServiceFileSystemUploadFileSuccess(),
                            new FileInputStream(source.toFile()),
                            target
                    )
            );

            Assertions.assertTrue(Files.exists(source));
            Assertions.assertTrue(Files.exists(target));
            Files.delete(source);
            Files.delete(target);
        }

        @RepeatedTest(10)
        public void uploadDirectorySuccess() throws IOException {
            final String titleDirectory = UUID.randomUUID().toString();
            final String titleFile = UUID.randomUUID() + ".txt";
            Path directory = Path.of(System.getProperty("java.io.tmpdir"), titleDirectory);
            Path fileInDirectory = Path.of(directory.toString(), titleFile);
            Path target = Path.of(tmpDirectory().toString(), titleDirectory);

            Files.createDirectory(directory);
            Files.createFile(fileInDirectory);

            Assertions.assertDoesNotThrow(
                    () -> AtomicSystemCallManager.call(
                            new MockServiceFileSystem.MockServiceFileSystemUploadDirectorySuccess(),
                            directory,
                            target
                    )
            );

            Assertions.assertTrue(Files.exists(target));
            Assertions.assertTrue(Files.exists(Path.of(tmpDirectory().toString(), titleDirectory, titleFile)));

            deleteDirectory(directory);
            deleteDirectory(target);
        }

        @RepeatedTest(10)
        public void uploadFileFail_directlyThrownExactException_handlerAtomicSuccess() throws IOException {
            final String title = UUID.randomUUID() + ".txt";
            Path source = Path.of(System.getProperty("java.io.tmpdir"), title);
            Path target = Path.of(tmpDirectory().toString(), title);
            Files.createFile(source);

            assertThatExceptionOfType(InvocationTargetException.class)
                    .isThrownBy(() -> AtomicSystemCallManager.call(
                            new MockServiceFileSystem.MockServiceFileSystemUploadFileFailException(),
                            new FileInputStream(source.toFile()),
                            target
                    ))
                    .withRootCauseInstanceOf(IOException.class);

            Assertions.assertTrue(Files.exists(source));
            Assertions.assertFalse(Files.exists(target));
            Files.delete(source);
        }

        @RepeatedTest(10)
        public void uploadDirectoryFail_directlyThrownExactException_handlerAtomicSuccess() throws IOException {
            final String titleDirectory = UUID.randomUUID().toString();
            final String titleFile = UUID.randomUUID() + ".txt";
            Path directory = Path.of(System.getProperty("java.io.tmpdir"), titleDirectory);
            Path fileInDirectory = Path.of(directory.toString(), titleFile);
            Path target = Path.of(tmpDirectory().toString(), titleDirectory);

            Files.createDirectory(directory);
            Files.createFile(fileInDirectory);

            assertThatExceptionOfType(InvocationTargetException.class)
                    .isThrownBy(() -> AtomicSystemCallManager.call(
                            new MockServiceFileSystem.MockServiceFileSystemUploadDirectoryFailException(),
                            directory,
                            target
                    ))
                    .withRootCauseInstanceOf(IOException.class);

            Assertions.assertFalse(Files.exists(target));
            Assertions.assertFalse(Files.exists(Path.of(tmpDirectory().toString(), titleDirectory, titleFile)));

            deleteDirectory(directory);
        }

        @RepeatedTest(10)
        public void uploadFileFail_directlyThrownOtherException_handlerAtomicFail() throws IOException {
            final String title = UUID.randomUUID() + ".txt";
            Path source = Path.of(System.getProperty("java.io.tmpdir"), title);
            Path target = Path.of(tmpDirectory().toString(), title);
            Files.createFile(source);

            assertThatExceptionOfType(InvocationTargetException.class)
                    .isThrownBy(() -> AtomicSystemCallManager.call(
                            new MockServiceFileSystem.MockServiceFileSystemUploadFileFailOtherException(),
                            new FileInputStream(source.toFile()),
                            target
                    ))
                    .withRootCauseInstanceOf(Exception.class);

            Assertions.assertTrue(Files.exists(source));
            Assertions.assertTrue(Files.exists(target));
            Files.delete(source);
            Files.delete(target);
        }

        @RepeatedTest(10)
        public void uploadDirectoryFail_directlyThrownOtherException_handlerAtomicFail() throws IOException {
            final String titleDirectory = UUID.randomUUID().toString();
            final String titleFile = UUID.randomUUID() + ".txt";
            Path directory = Path.of(System.getProperty("java.io.tmpdir"), titleDirectory);
            Path fileInDirectory = Path.of(directory.toString(), titleFile);
            Path target = Path.of(tmpDirectory().toString(), titleDirectory);

            Files.createDirectory(directory);
            Files.createFile(fileInDirectory);

            assertThatExceptionOfType(InvocationTargetException.class)
                    .isThrownBy(() -> AtomicSystemCallManager.call(
                            new MockServiceFileSystem.MockServiceFileSystemUploadDirectoryFailOtherException(),
                            directory,
                            target
                    ))
                    .withRootCauseInstanceOf(Exception.class);

            Assertions.assertTrue(Files.exists(target));
            Assertions.assertTrue(Files.exists(Path.of(tmpDirectory().toString(), titleDirectory, titleFile)));

            deleteDirectory(directory);
            deleteDirectory(target);
        }

        @RepeatedTest(10)
        public void uploadFileFail_directoryThrownMultiException_handlerAtomicSuccess() throws IOException {
            final String title = UUID.randomUUID() + ".txt";
            Path source = Path.of(System.getProperty("java.io.tmpdir"), title);
            Path target = Path.of(tmpDirectory().toString(), title);
            Files.createFile(source);

            assertThatExceptionOfType(InvocationTargetException.class)
                    .isThrownBy(() -> AtomicSystemCallManager.call(
                            new MockServiceFileSystem.MockServiceFileSystemUploadFileFailMultiException(),
                            new FileInputStream(source.toFile()),
                            target
                    ))
                    .withRootCauseInstanceOf(Exception.class);

            Assertions.assertTrue(Files.exists(source));
            Assertions.assertFalse(Files.exists(target));
            Files.delete(source);
        }

        @RepeatedTest(10)
        public void uploadDirectoryFail_directoryThrownMultiException_handlerAtomicSuccess() throws IOException {
            final String titleDirectory = UUID.randomUUID().toString();
            final String titleFile = UUID.randomUUID() + ".txt";
            Path directory = Path.of(System.getProperty("java.io.tmpdir"), titleDirectory);
            Path fileInDirectory = Path.of(directory.toString(), titleFile);
            Path target = Path.of(tmpDirectory().toString(), titleDirectory);

            Files.createDirectory(directory);
            Files.createFile(fileInDirectory);

            assertThatExceptionOfType(InvocationTargetException.class)
                    .isThrownBy(() -> AtomicSystemCallManager.call(
                            new MockServiceFileSystem.MockServiceFileSystemUploadDirectoryFailMultiException(),
                            directory,
                            target
                    ))
                    .withRootCauseInstanceOf(Exception.class);

            Assertions.assertFalse(Files.exists(target));
            Assertions.assertFalse(Files.exists(Path.of(tmpDirectory().toString(), titleDirectory, titleFile)));

            deleteDirectory(directory);
        }

        @RepeatedTest(10)
        public void uploadFileFail_directoryThrownOtherMethodException_handlerAtomicSuccess() throws Exception {
            final String title = UUID.randomUUID() + ".txt";
            Path source = Path.of(System.getProperty("java.io.tmpdir"), title);
            Path target = Path.of(tmpDirectory().toString(), title);
            Files.createFile(source);

            assertThatExceptionOfType(InvocationTargetException.class)
                    .isThrownBy(() -> AtomicSystemCallManager.call(
                            new MockServiceFileSystem.MockServiceFileSystemUploadFileFailOtherMethodException(),
                            new FileInputStream(source.toFile()),
                            target
                    ))
                    .withRootCauseInstanceOf(IOException.class);

            Assertions.assertTrue(Files.exists(source));
            Assertions.assertFalse(Files.exists(target));
            Files.delete(source);
        }

        @RepeatedTest(10)
        public void uploadDirectoryFail_directoryThrownOtherMethodException_handlerAtomicSuccess() throws Exception {
            final String titleDirectory = UUID.randomUUID().toString();
            final String titleFile = UUID.randomUUID() + ".txt";
            Path directory = Path.of(System.getProperty("java.io.tmpdir"), titleDirectory);
            Path fileInDirectory = Path.of(directory.toString(), titleFile);
            Path target = Path.of(tmpDirectory().toString(), titleDirectory);

            Files.createDirectory(directory);
            Files.createFile(fileInDirectory);

            assertThatExceptionOfType(InvocationTargetException.class)
                    .isThrownBy(() -> AtomicSystemCallManager.call(
                            new MockServiceFileSystem.MockServiceFileSystemUploadDirectoryFailOtherMethodException(),
                            directory,
                            target
                    ))
                    .withRootCauseInstanceOf(IOException.class);

            Assertions.assertFalse(Files.exists(target));
            Assertions.assertFalse(Files.exists(Path.of(tmpDirectory().toString(), titleDirectory, titleFile)));

            deleteDirectory(directory);
        }

    }

}
