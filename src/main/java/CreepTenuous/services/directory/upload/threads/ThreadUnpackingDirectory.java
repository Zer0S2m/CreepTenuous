package CreepTenuous.services.directory.upload.threads;

import CreepTenuous.services.directory.builder.enums.Directory;

import java.io.*;
import java.nio.file.Path;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class ThreadUnpackingDirectory extends Thread {
    private final List<Path> files;
    private final Path outputDirectory;
    private final byte[] buffer = new byte[(int) Directory.BYTES_UNPACKING_ZIP.getInt()];

    public ThreadUnpackingDirectory(String name, List<Path> files, Path outputDirectory) {
        this.setName(name);
        this.files = files;
        this.outputDirectory = outputDirectory;
    }

    public void run() {
        for (Path file : files) {
            File outputDirectoryFile = new File(outputDirectory.toString());
            File realFile = file.toFile();
            try (ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(realFile))) {
                unpacking(zipInputStream, outputDirectoryFile);
                realFile.delete();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void unpacking(
            ZipInputStream zipInputStream,
            File outputDirectoryFile
    ) throws IOException {
        ZipEntry zipEntryDir = zipInputStream.getNextEntry();
        while (zipEntryDir != null) {
            File newFile = newFile(outputDirectoryFile, zipEntryDir);
            if (zipEntryDir.isDirectory()) {
                if (!newFile.isDirectory() && !newFile.mkdirs()) {
                    throw new IOException("Failed to create directory " + newFile);
                }
            } else {
                File parent = newFile.getParentFile();
                if (!parent.isDirectory() && !parent.mkdirs()) {
                    throw new IOException("Failed to create directory " + parent);
                }

                try (FileOutputStream fos = new FileOutputStream(newFile)) {
                    int len;
                    while ((len = zipInputStream.read(buffer)) > 0) {
                        fos.write(buffer, 0, len);
                    }
                }
            }
            zipEntryDir = zipInputStream.getNextEntry();
        }
    }

    public static File newFile(File destinationDir, ZipEntry zipEntryDir) throws IOException {
        File destFile = new File(destinationDir, zipEntryDir.getName());

        String destDirPath = destinationDir.getCanonicalPath();
        String destFilePath = destFile.getCanonicalPath();

        if (!destFilePath.startsWith(destDirPath + File.separator)) {
            throw new IOException("Entry is outside of the target dir: " + zipEntryDir.getName());
        }

        return destFile;
    }
}
