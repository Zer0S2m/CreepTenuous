package CreepTenuous.Api.Files.CreateFile;

import CreepTenuous.Api.Files.CreateFile.data.DataCreateFile;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/file")
@RestController
public class CreateFile {
    @PostMapping("/create")
    @ResponseStatus(code = HttpStatus.CREATED)
    public void deleteFile(@RequestBody DataCreateFile file) {

    }
}
