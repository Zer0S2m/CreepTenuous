package com.zer0s2m.creeptenuous.core.services;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.jetbrains.annotations.NotNull;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;

public interface ServiceCreateFileExcel {

    /**
     * Create xlsx file
     * @param path target path
     */
    default void createFileExcel(@NotNull Path path) {
        try (HSSFWorkbook workbook = new HSSFWorkbook()) {
            workbook.createSheet("sheet");
            try (FileOutputStream out = new FileOutputStream(path.toFile())) {
                workbook.write(out);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
