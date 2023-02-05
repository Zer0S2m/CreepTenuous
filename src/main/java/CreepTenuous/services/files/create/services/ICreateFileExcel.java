package CreepTenuous.services.files.create.services;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;

public interface ICreateFileExcel {
    default void createFileExcel(Path path) {
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
