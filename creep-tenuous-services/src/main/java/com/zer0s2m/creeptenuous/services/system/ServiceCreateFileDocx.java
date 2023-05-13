package com.zer0s2m.creeptenuous.services.system;

import org.apache.poi.xwpf.usermodel.XWPFDocument;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;

public interface ServiceCreateFileDocx {
    default void createFileDocx(Path path) {
        try (XWPFDocument docx = new XWPFDocument()) {
            try (FileOutputStream out = new FileOutputStream(path.toFile())) {
                docx.write(out);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
