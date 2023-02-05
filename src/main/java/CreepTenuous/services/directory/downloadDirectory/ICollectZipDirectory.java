package CreepTenuous.services.directory.downloadDirectory;

import CreepTenuous.services.directory.builderDirectory.enums.Directory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public interface ICollectZipDirectory {
    default Path collectZip(Path path) throws RuntimeException {
        String pathToZip = path.getParent() + Directory.SEPARATOR.get() +
                path.getFileName().toString() + Directory.EXTENSION_FILE_ZIP.get();
        try (
                final FileOutputStream fosFile = new FileOutputStream(pathToZip);
                final ZipOutputStream fosZip = new ZipOutputStream(fosFile)
        ) {
            File newFileZip = new File(path.toString());
            collectZipFile(newFileZip, newFileZip.getName(), fosZip);
            return Paths.get(pathToZip);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void collectZipFile(File newFileZip, String filenameZip, ZipOutputStream fosZip) throws IOException {
        if (newFileZip.isHidden()) {
            return;
        }

        if (newFileZip.isDirectory()) {
            if (filenameZip.endsWith(Directory.SEPARATOR.get())) {
                fosZip.putNextEntry(new ZipEntry(filenameZip));
                fosZip.closeEntry();
            } else {
                fosZip.putNextEntry(new ZipEntry(filenameZip + Directory.SEPARATOR.get()));
                fosZip.closeEntry();
            }

            File[] children = newFileZip.listFiles();

            for (File childFile : children) {
                collectZipFile(
                        childFile,
                        filenameZip + Directory.SEPARATOR.get() + childFile.getName(),
                        fosZip
                );
            }
            return;
        }

        try (FileInputStream fisZip = new FileInputStream(newFileZip)) {
            ZipEntry zipEntry = new ZipEntry(filenameZip);
            fosZip.putNextEntry(zipEntry);

            final byte[] bytes = new byte[(int) Directory.BYTES_COLLECT_ZIP.getInt()];
            int length;
            while ((length = fisZip.read(bytes)) >= 0) {
                fosZip.write(bytes, 0, length);
            }
        };
    }
}
