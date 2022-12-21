package CreepTenuous.api.controllers.files.createFile;

import CreepTenuous.api.controllers.files.createFile.data.DataCreateFile;
import CreepTenuous.api.core.version.v1.V1APIController;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@V1APIController
public class CreateFileApi {
    @PostMapping("/file/create")
    @ResponseStatus(code = HttpStatus.CREATED)
    public void deleteFile(@RequestBody DataCreateFile file) {

    }
}
