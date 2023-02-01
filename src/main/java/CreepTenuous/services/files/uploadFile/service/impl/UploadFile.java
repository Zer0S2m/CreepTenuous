package CreepTenuous.services.files.uploadFile.service.impl;

import CreepTenuous.services.directory.builderDirectory.enums.Directory;
import CreepTenuous.services.directory.utils.build.BuildDirectoryPath;
import CreepTenuous.services.files.uploadFile.service.IUploadFile;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Service("upload-file")
public class UploadFile implements IUploadFile {
    @Autowired
    private BuildDirectoryPath buildDirectoryPath;

    @Override
    public void upload(List<MultipartFile> files, List<String> parents) throws IOException {
        Path path = Paths.get(buildDirectoryPath.build(parents));
        System.out.println(files);
    }
}
