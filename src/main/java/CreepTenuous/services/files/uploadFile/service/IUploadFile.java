package CreepTenuous.services.files.uploadFile.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface IUploadFile {
    void upload(List<MultipartFile> files, List<String> parents) throws IOException;
}
