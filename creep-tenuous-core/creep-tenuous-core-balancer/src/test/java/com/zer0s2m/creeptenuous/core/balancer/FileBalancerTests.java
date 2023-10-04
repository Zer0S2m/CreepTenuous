package com.zer0s2m.creeptenuous.core.balancer;

import com.zer0s2m.creeptenuous.core.balancer.exceptions.FileIsDirectoryException;
import com.zer0s2m.creeptenuous.starter.test.annotations.TestTagCore;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.*;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

@TestTagCore
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
public class FileBalancerTests {

    String pathRoot = System.getenv("CT_ROOT_PATH");

    @BeforeEach
    public void init() {
        if (pathRoot == null || pathRoot.length() == 0) {
            throw new RuntimeException("Environment variable not set: [CT_ROOT_PATH]");
        }
    }

    private @NotNull Path initFile() throws IOException {
        String str = "test".repeat(100);

        String systemNameFile = UUID.randomUUID().toString();
        Path file = Path.of(pathRoot, systemNameFile);

        try (FileOutputStream fileOutputStream = new FileOutputStream(file.toFile())) {
            fileOutputStream.write(str.getBytes(), 0, str.length());
        }

        return file;
    }

    @Test
    public void split_success() throws IOException {
        Path file = initFile();
        String systemNameFile = file.getFileName().toString();

        Collection<Path> paths = Assertions.assertDoesNotThrow(() -> FileBalancer.split(file));

        Assertions.assertEquals(FileBalancer.PART_COUNTER, paths.size());
        Assertions.assertTrue(Files.exists(file));
        Assertions.assertTrue(Files.exists(Path.of(
                pathRoot, String.format(FileBalancer.FORMAT_OUTPUT_FILE, systemNameFile, 1))));
        Assertions.assertTrue(Files.exists(Path.of(
                pathRoot, String.format(FileBalancer.FORMAT_OUTPUT_FILE, systemNameFile, 2))));
        Assertions.assertTrue(Files.exists(Path.of(
                pathRoot, String.format(FileBalancer.FORMAT_OUTPUT_FILE, systemNameFile, 3))));
        Assertions.assertTrue(Files.exists(Path.of(
                pathRoot, String.format(FileBalancer.FORMAT_OUTPUT_FILE, systemNameFile, 4))));
        Assertions.assertTrue(Files.exists(Path.of(
                pathRoot, String.format(FileBalancer.FORMAT_OUTPUT_FILE, systemNameFile, 5))));
    }

    @Test
    public void split_success_partCountOther() throws IOException {
        Path file = initFile();
        String systemNameFile = file.getFileName().toString();
        int partCounter = 2;

        Collection<Path> paths = Assertions.assertDoesNotThrow(() -> FileBalancer.split(file, partCounter));

        Assertions.assertEquals(partCounter, paths.size());
        Assertions.assertTrue(Files.exists(file));
        Assertions.assertTrue(Files.exists(Path.of(
                pathRoot, String.format(FileBalancer.FORMAT_OUTPUT_FILE, systemNameFile, 1))));
        Assertions.assertTrue(Files.exists(Path.of(
                pathRoot, String.format(FileBalancer.FORMAT_OUTPUT_FILE, systemNameFile, 2))));
    }

    @Test
    public void split_fail_fileObjectIsDirectory() throws IOException {
        Path directory = Path.of(pathRoot, UUID.randomUUID().toString());
        Files.createDirectory(directory);

        Assertions.assertThrows(
                FileIsDirectoryException.class,
                () -> FileBalancer.split(directory));

        Files.delete(directory);
    }

    @Test
    public void merge_success() throws IOException {
        Path file = initFile();
        Path systemNameFileInto = Path.of(pathRoot, UUID.randomUUID().toString());

        Collection<Path> paths = Assertions.assertDoesNotThrow(() -> FileBalancer.split(file));

        Path into = Assertions.assertDoesNotThrow(() -> FileBalancer.merge(paths, systemNameFileInto));

        Assertions.assertTrue(Files.exists(into));
        Assertions.assertFalse(Files.isDirectory(into));
    }

    @Test
    public void merge_fail_fileObjectIsDirectory() throws IOException {
        Path directory = Path.of(pathRoot, UUID.randomUUID().toString());
        Files.createDirectory(directory);

        Assertions.assertThrows(
                FileIsDirectoryException.class,
                () -> FileBalancer.merge(List.of(directory), directory));

        Files.delete(directory);
    }

}
