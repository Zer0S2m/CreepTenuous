package CreepTenuous.Api.controllers.Files.CreateFile;

import CreepTenuous.Api.controllers.Files.CreateFile.data.DataCreateFile;
import CreepTenuous.Api.core.version.v1.V1APIController;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@V1APIController
public class CreateFile {
    @PostMapping("/file/create")
    @ResponseStatus(code = HttpStatus.CREATED)
    public void deleteFile(@RequestBody DataCreateFile file) {

    }
}
