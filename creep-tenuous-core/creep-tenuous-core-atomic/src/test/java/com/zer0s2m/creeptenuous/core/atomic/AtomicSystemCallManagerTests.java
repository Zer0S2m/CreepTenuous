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
        public void deleteFileFail_directoryThrownExactException_handlerAtomicSuccess() throws IOException {
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
        public void deleteDirectoryFail_directoryThrownExactException_handlerAtomicSuccess() throws IOException {
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
        public void deleteFileFail_directoryThrownOtherException_handlerAtomicFail() throws IOException {
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
        public void deleteDirectoryFail_directoryThrownOtherException_handlerAtomicFail() throws IOException {
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
        public void deleteFileFail_directoryThrownMultiException_handlerAtomicSuccess() throws IOException {
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
        public void deleteDirectoryFail_directoryThrownMultiException_handlerAtomicSuccess() throws IOException {
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
        public void createFileFail_directoryThrownExactException_handlerAtomicSuccess() {
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
        public void createFileFail_directoryThrownOtherException_handlerAtomicFail() throws IOException {
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
        public void createFileFail_directoryThrownMultiException_handlerAtomicSuccess() {
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
        public void createFileFail_directoryThrownOtherMethodException_handlerAtomicSuccess() {
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
        public void uploadFileFail_directoryThrownExactException_handlerAtomicSuccess() throws IOException {
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
        public void uploadDirectoryFail_directoryThrownExactException_handlerAtomicSuccess() throws IOException {
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
        public void uploadFileFail_directoryThrownOtherException_handlerAtomicFail() throws IOException {
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
        public void uploadDirectoryFail_directoryThrownOtherException_handlerAtomicFail() throws IOException {
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

    @Nested
    class AtomicSystemCallManagerTestsOperationCopy {

        @RepeatedTest(10)
        public void copyFileSuccess() throws IOException {
            final String title = UUID.randomUUID() + ".txt";
            Path source = Path.of(System.getProperty("java.io.tmpdir"), title);
            Path target = Path.of(tmpDirectory().toString(), title);
            Files.createFile(source);

            Assertions.assertDoesNotThrow(
                    () -> AtomicSystemCallManager.call(
                            new MockServiceFileSystem.MockServiceFileSystemCopyFileSuccess(),
                            source,
                            target
                    )
            );

            Assertions.assertTrue(Files.exists(source));
            Assertions.assertTrue(Files.exists(target));
            Files.delete(source);
            Files.delete(target);
        }

        @RepeatedTest(10)
        public void copyDirectorySuccess() throws IOException {
            final String titleDirectory = UUID.randomUUID().toString();
            final String titleFile = UUID.randomUUID() + ".txt";
            Path directory = Path.of(System.getProperty("java.io.tmpdir"), titleDirectory);
            Path fileInDirectory = Path.of(directory.toString(), titleFile);
            Path target = Path.of(tmpDirectory().toString(), titleDirectory);

            Files.createDirectory(directory);
            Files.createFile(fileInDirectory);

            Assertions.assertDoesNotThrow(
                    () -> AtomicSystemCallManager.call(
                            new MockServiceFileSystem.MockServiceFileSystemCopyDirectorySuccess(),
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
        public void copyFileFail_directoryThrownExactException_handlerAtomicSuccess() throws IOException {
            final String title = UUID.randomUUID() + ".txt";
            Path source = Path.of(System.getProperty("java.io.tmpdir"), title);
            Path target = Path.of(tmpDirectory().toString(), title);
            Files.createFile(source);

            assertThatExceptionOfType(InvocationTargetException.class)
                    .isThrownBy(() -> AtomicSystemCallManager.call(
                            new MockServiceFileSystem.MockServiceFileSystemCopyFileFailException(),
                            source,
                            target
                    ))
                    .withRootCauseInstanceOf(IOException.class);

            Assertions.assertTrue(Files.exists(source));
            Assertions.assertFalse(Files.exists(target));
            Files.delete(source);
        }

        @RepeatedTest(10)
        public void copyDirectoryFail_directoryThrownExactException_handlerAtomicSuccess() throws IOException {
            final String titleDirectory = UUID.randomUUID().toString();
            final String titleFile = UUID.randomUUID() + ".txt";
            Path directory = Path.of(System.getProperty("java.io.tmpdir"), titleDirectory);
            Path fileInDirectory = Path.of(directory.toString(), titleFile);
            Path target = Path.of(tmpDirectory().toString(), titleDirectory);

            Files.createDirectory(directory);
            Files.createFile(fileInDirectory);

            assertThatExceptionOfType(InvocationTargetException.class)
                    .isThrownBy(() -> AtomicSystemCallManager.call(
                            new MockServiceFileSystem.MockServiceFileSystemCopyDirectoryFailException(),
                            directory,
                            target
                    ))
                    .withRootCauseInstanceOf(IOException.class);

            Assertions.assertFalse(Files.exists(target));
            Assertions.assertFalse(Files.exists(Path.of(tmpDirectory().toString(), titleDirectory, titleFile)));

            deleteDirectory(directory);
        }

        @RepeatedTest(10)
        public void copyFileFail_directoryThrownOtherException_handlerAtomicFail() throws IOException {
            final String title = UUID.randomUUID() + ".txt";
            Path source = Path.of(System.getProperty("java.io.tmpdir"), title);
            Path target = Path.of(tmpDirectory().toString(), title);
            Files.createFile(source);

            assertThatExceptionOfType(InvocationTargetException.class)
                    .isThrownBy(() -> AtomicSystemCallManager.call(
                            new MockServiceFileSystem.MockServiceFileSystemCopyFileFailOtherException(),
                            source,
                            target
                    ))
                    .withRootCauseInstanceOf(Exception.class);

            Assertions.assertTrue(Files.exists(source));
            Assertions.assertTrue(Files.exists(target));
            Files.delete(source);
            Files.delete(target);
        }

        @RepeatedTest(10)
        public void copyDirectoryFail_directoryThrownOtherException_handlerAtomicFail() throws IOException {
            final String titleDirectory = UUID.randomUUID().toString();
            final String titleFile = UUID.randomUUID() + ".txt";
            Path directory = Path.of(System.getProperty("java.io.tmpdir"), titleDirectory);
            Path fileInDirectory = Path.of(directory.toString(), titleFile);
            Path target = Path.of(tmpDirectory().toString(), titleDirectory);

            Files.createDirectory(directory);
            Files.createFile(fileInDirectory);

            assertThatExceptionOfType(InvocationTargetException.class)
                    .isThrownBy(() -> AtomicSystemCallManager.call(
                            new MockServiceFileSystem.MockServiceFileSystemCopyDirectoryFailOtherException(),
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
        public void copyFileFail_directoryThrownMultiException_handlerAtomicSuccess() throws IOException {
            final String title = UUID.randomUUID() + ".txt";
            Path source = Path.of(System.getProperty("java.io.tmpdir"), title);
            Path target = Path.of(tmpDirectory().toString(), title);
            Files.createFile(source);

            assertThatExceptionOfType(InvocationTargetException.class)
                    .isThrownBy(() -> AtomicSystemCallManager.call(
                            new MockServiceFileSystem.MockServiceFileSystemCopyFileFailMultiException(),
                            source,
                            target
                    ))
                    .withRootCauseInstanceOf(Exception.class);

            Assertions.assertTrue(Files.exists(source));
            Assertions.assertFalse(Files.exists(target));
            Files.delete(source);
        }

        @RepeatedTest(10)
        public void copyDirectoryFail_directoryThrownMultiException_handlerAtomicSuccess() throws IOException {
            final String titleDirectory = UUID.randomUUID().toString();
            final String titleFile = UUID.randomUUID() + ".txt";
            Path directory = Path.of(System.getProperty("java.io.tmpdir"), titleDirectory);
            Path fileInDirectory = Path.of(directory.toString(), titleFile);
            Path target = Path.of(tmpDirectory().toString(), titleDirectory);

            Files.createDirectory(directory);
            Files.createFile(fileInDirectory);

            assertThatExceptionOfType(InvocationTargetException.class)
                    .isThrownBy(() -> AtomicSystemCallManager.call(
                            new MockServiceFileSystem.MockServiceFileSystemCopyDirectoryFailMultiException(),
                            directory,
                            target
                    ))
                    .withRootCauseInstanceOf(Exception.class);

            Assertions.assertFalse(Files.exists(target));
            Assertions.assertFalse(Files.exists(Path.of(tmpDirectory().toString(), titleDirectory, titleFile)));

            deleteDirectory(directory);
        }

        @RepeatedTest(10)
        public void copyFileFail_directoryThrownOtherMethodException_handlerAtomicSuccess() throws IOException {
            final String title = UUID.randomUUID() + ".txt";
            Path source = Path.of(System.getProperty("java.io.tmpdir"), title);
            Path target = Path.of(tmpDirectory().toString(), title);
            Files.createFile(source);

            assertThatExceptionOfType(InvocationTargetException.class)
                    .isThrownBy(() -> AtomicSystemCallManager.call(
                            new MockServiceFileSystem.MockServiceFileSystemCopyFileFailOtherMethodException(),
                            source,
                            target
                    ))
                    .withRootCauseInstanceOf(Exception.class);

            Assertions.assertTrue(Files.exists(source));
            Assertions.assertFalse(Files.exists(target));
            Files.delete(source);
        }

        @RepeatedTest(10)
        public void copyDirectoryFail_directoryThrownOtherMethodException_handlerAtomicSuccess() throws IOException {
            final String titleDirectory = UUID.randomUUID().toString();
            final String titleFile = UUID.randomUUID() + ".txt";
            Path directory = Path.of(System.getProperty("java.io.tmpdir"), titleDirectory);
            Path fileInDirectory = Path.of(directory.toString(), titleFile);
            Path target = Path.of(tmpDirectory().toString(), titleDirectory);

            Files.createDirectory(directory);
            Files.createFile(fileInDirectory);

            assertThatExceptionOfType(InvocationTargetException.class)
                    .isThrownBy(() -> AtomicSystemCallManager.call(
                            new MockServiceFileSystem.MockServiceFileSystemCopyDirectoryFailOtherMethodException(),
                            directory,
                            target
                    ))
                    .withRootCauseInstanceOf(Exception.class);

            Assertions.assertFalse(Files.exists(target));
            Assertions.assertFalse(Files.exists(Path.of(tmpDirectory().toString(), titleDirectory, titleFile)));

            deleteDirectory(directory);
        }

    }

    @Nested
    class AtomicSystemCallManagerTestsOperationMove {

        @RepeatedTest(10)
        public void moveFileSuccess() throws IOException {
            final String title = UUID.randomUUID() + ".txt";
            Path source = Path.of(System.getProperty("java.io.tmpdir"), title);
            Path target = Path.of(tmpDirectory().toString(), title);
            Files.createFile(source);

            Assertions.assertDoesNotThrow(
                    () -> AtomicSystemCallManager.call(
                            new MockServiceFileSystem.MockServiceFileSystemMoveFileSuccess(),
                            source,
                            target
                    )
            );

            Assertions.assertFalse(Files.exists(source));
            Assertions.assertTrue(Files.exists(target));
            Files.delete(target);
        }

        @RepeatedTest(10)
        public void moveDirectorySuccess() throws IOException {
            final String titleDirectory = UUID.randomUUID().toString();
            final String titleFile = UUID.randomUUID() + ".txt";
            Path directory = Path.of(System.getProperty("java.io.tmpdir"), titleDirectory);
            Path fileInDirectory = Path.of(directory.toString(), titleFile);
            Path target = Path.of(tmpDirectory().toString(), titleDirectory);

            Files.createDirectory(directory);
            Files.createFile(fileInDirectory);

            Assertions.assertDoesNotThrow(
                    () -> AtomicSystemCallManager.call(
                            new MockServiceFileSystem.MockServiceFileSystemMoveDirectorySuccess(),
                            directory,
                            target
                    )
            );

            Assertions.assertFalse(Files.exists(directory));
            Assertions.assertFalse(Files.exists(fileInDirectory));
            Assertions.assertTrue(Files.exists(target));
            Assertions.assertTrue(Files.exists(Path.of(tmpDirectory().toString(), titleDirectory, titleFile)));

            deleteDirectory(target);
        }

        @RepeatedTest(10)
        public void moveFileFail_directoryThrownExactException_handlerAtomicSuccess() throws IOException {
            final String title = UUID.randomUUID() + ".txt";
            Path source = Path.of(System.getProperty("java.io.tmpdir"), title);
            Path target = Path.of(tmpDirectory().toString(), title);
            Files.createFile(source);

            assertThatExceptionOfType(InvocationTargetException.class)
                    .isThrownBy(() -> AtomicSystemCallManager.call(
                            new MockServiceFileSystem.MockServiceFileSystemMoveFileFailException(),
                            source,
                            target
                    ))
                    .withRootCauseInstanceOf(IOException.class);

            Assertions.assertTrue(Files.exists(source));
            Assertions.assertFalse(Files.exists(target));
            Files.delete(source);
        }

        @RepeatedTest(10)
        public void moveDirectoryFail_directoryThrownExactException_handlerAtomicSuccess() throws IOException {
            final String titleDirectory = UUID.randomUUID().toString();
            final String titleFile = UUID.randomUUID() + ".txt";
            Path directory = Path.of(System.getProperty("java.io.tmpdir"), titleDirectory);
            Path fileInDirectory = Path.of(directory.toString(), titleFile);
            Path target = Path.of(tmpDirectory().toString(), titleDirectory);

            Files.createDirectory(directory);
            Files.createFile(fileInDirectory);

            assertThatExceptionOfType(InvocationTargetException.class)
                    .isThrownBy(() -> AtomicSystemCallManager.call(
                            new MockServiceFileSystem.MockServiceFileSystemMoveDirectoryFailException(),
                            directory,
                            target
                    ))
                    .withRootCauseInstanceOf(IOException.class);

            Assertions.assertTrue(Files.exists(directory));
            Assertions.assertTrue(Files.exists(fileInDirectory));
            Assertions.assertFalse(Files.exists(target));
            Assertions.assertFalse(Files.exists(Path.of(tmpDirectory().toString(), titleDirectory, titleFile)));

            deleteDirectory(directory);
        }

        @RepeatedTest(10)
        public void moveFileFail_directoryThrownOtherException_handlerAtomicFail() throws Exception {
            final String title = UUID.randomUUID() + ".txt";
            Path source = Path.of(System.getProperty("java.io.tmpdir"), title);
            Path target = Path.of(tmpDirectory().toString(), title);
            Files.createFile(source);

            assertThatExceptionOfType(InvocationTargetException.class)
                    .isThrownBy(() -> AtomicSystemCallManager.call(
                            new MockServiceFileSystem.MockServiceFileSystemMoveFileFailOtherException(),
                            source,
                            target
                    ))
                    .withRootCauseInstanceOf(Exception.class);

            Assertions.assertFalse(Files.exists(source));
            Assertions.assertTrue(Files.exists(target));
            Files.delete(target);
        }

        @RepeatedTest(10)
        public void moveDirectoryFail_directoryThrownOtherException_handlerAtomicFail() throws Exception {
            final String titleDirectory = UUID.randomUUID().toString();
            final String titleFile = UUID.randomUUID() + ".txt";
            Path directory = Path.of(System.getProperty("java.io.tmpdir"), titleDirectory);
            Path fileInDirectory = Path.of(directory.toString(), titleFile);
            Path target = Path.of(tmpDirectory().toString(), titleDirectory);

            Files.createDirectory(directory);
            Files.createFile(fileInDirectory);

            assertThatExceptionOfType(InvocationTargetException.class)
                    .isThrownBy(() -> AtomicSystemCallManager.call(
                            new MockServiceFileSystem.MockServiceFileSystemMoveDirectoryFailOtherException(),
                            directory,
                            target
                    ))
                    .withRootCauseInstanceOf(Exception.class);

            Assertions.assertFalse(Files.exists(directory));
            Assertions.assertFalse(Files.exists(fileInDirectory));
            Assertions.assertTrue(Files.exists(target));
            Assertions.assertTrue(Files.exists(Path.of(tmpDirectory().toString(), titleDirectory, titleFile)));

            deleteDirectory(target);
        }

        @RepeatedTest(10)
        public void moveFileFail_directoryThrownMultiException_handlerAtomicSuccess() throws IOException {
            final String title = UUID.randomUUID() + ".txt";
            Path source = Path.of(System.getProperty("java.io.tmpdir"), title);
            Path target = Path.of(tmpDirectory().toString(), title);
            Files.createFile(source);

            assertThatExceptionOfType(InvocationTargetException.class)
                    .isThrownBy(() -> AtomicSystemCallManager.call(
                            new MockServiceFileSystem.MockServiceFileSystemMoveFileFailMultiException(),
                            source,
                            target
                    ))
                    .withRootCauseInstanceOf(IOException.class);

            Assertions.assertTrue(Files.exists(source));
            Assertions.assertFalse(Files.exists(target));
            Files.delete(source);
        }

        @RepeatedTest(10)
        public void moveDirectoryFail_directoryThrownMultiException_handlerAtomicSuccess() throws IOException {
            final String titleDirectory = UUID.randomUUID().toString();
            final String titleFile = UUID.randomUUID() + ".txt";
            Path directory = Path.of(System.getProperty("java.io.tmpdir"), titleDirectory);
            Path fileInDirectory = Path.of(directory.toString(), titleFile);
            Path target = Path.of(tmpDirectory().toString(), titleDirectory);

            Files.createDirectory(directory);
            Files.createFile(fileInDirectory);

            assertThatExceptionOfType(InvocationTargetException.class)
                    .isThrownBy(() -> AtomicSystemCallManager.call(
                            new MockServiceFileSystem.MockServiceFileSystemMoveDirectoryFailMultiException(),
                            directory,
                            target
                    ))
                    .withRootCauseInstanceOf(IOException.class);

            Assertions.assertTrue(Files.exists(directory));
            Assertions.assertTrue(Files.exists(fileInDirectory));
            Assertions.assertFalse(Files.exists(target));
            Assertions.assertFalse(Files.exists(Path.of(tmpDirectory().toString(), titleDirectory, titleFile)));

            deleteDirectory(directory);
        }

        @RepeatedTest(10)
        public void moveFileFail_directoryThrownOtherMethodException_handlerAtomicSuccess() throws IOException {
            final String title = UUID.randomUUID() + ".txt";
            Path source = Path.of(System.getProperty("java.io.tmpdir"), title);
            Path target = Path.of(tmpDirectory().toString(), title);
            Files.createFile(source);

            assertThatExceptionOfType(InvocationTargetException.class)
                    .isThrownBy(() -> AtomicSystemCallManager.call(
                            new MockServiceFileSystem.MockServiceFileSystemMoveFileFailOtherMethodException(),
                            source,
                            target
                    ))
                    .withRootCauseInstanceOf(IOException.class);

            Assertions.assertTrue(Files.exists(source));
            Assertions.assertFalse(Files.exists(target));
            Files.delete(source);
        }

        @RepeatedTest(10)
        public void moveDirectoryFail_directoryThrownOtherMethodException_handlerAtomicSuccess() throws IOException {
            final String titleDirectory = UUID.randomUUID().toString();
            final String titleFile = UUID.randomUUID() + ".txt";
            Path directory = Path.of(System.getProperty("java.io.tmpdir"), titleDirectory);
            Path fileInDirectory = Path.of(directory.toString(), titleFile);
            Path target = Path.of(tmpDirectory().toString(), titleDirectory);

            Files.createDirectory(directory);
            Files.createFile(fileInDirectory);

            assertThatExceptionOfType(InvocationTargetException.class)
                    .isThrownBy(() -> AtomicSystemCallManager.call(
                            new MockServiceFileSystem.MockServiceFileSystemMoveDirectoryFailOtherMethodException(),
                            directory,
                            target
                    ))
                    .withRootCauseInstanceOf(IOException.class);

            Assertions.assertTrue(Files.exists(directory));
            Assertions.assertTrue(Files.exists(fileInDirectory));
            Assertions.assertFalse(Files.exists(target));
            Assertions.assertFalse(Files.exists(Path.of(tmpDirectory().toString(), titleDirectory, titleFile)));

            deleteDirectory(directory);
        }

    }

}
