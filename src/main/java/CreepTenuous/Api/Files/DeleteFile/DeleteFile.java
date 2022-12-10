package CreepTenuous.Api.Files.DeleteFile;

import CreepTenuous.Api.Files.DeleteFile.data.DataDeleteFile;
import CreepTenuous.Api.core.version.v1.V1APIController;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@V1APIController
public class DeleteFile {
    @DeleteMapping("/file/delete")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void deleteFile(@RequestBody DataDeleteFile file) {

    }
}
