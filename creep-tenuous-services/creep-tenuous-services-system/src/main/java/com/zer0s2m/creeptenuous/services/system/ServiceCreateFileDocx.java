package com.zer0s2m.creeptenuous.services.system;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.jetbrains.annotations.NotNull;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;

public interface ServiceCreateFileDocx {

    /**
     * Create docx file
     * @param path target path
     */
    default void createFileDocx(@NotNull Path path) {
        try (XWPFDocument docx = new XWPFDocument()) {
            try (FileOutputStream out = new FileOutputStream(path.toFile())) {
                docx.write(out);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
