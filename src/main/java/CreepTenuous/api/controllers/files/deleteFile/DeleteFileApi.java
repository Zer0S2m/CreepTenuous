package CreepTenuous.api.controllers.files.deleteFile;

import CreepTenuous.api.controllers.files.deleteFile.data.DataDeleteFile;
import CreepTenuous.api.core.version.v1.V1APIController;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@V1APIController
public class DeleteFileApi {
    @DeleteMapping("/file/delete")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void deleteFile(@RequestBody DataDeleteFile file) {

    }
}
