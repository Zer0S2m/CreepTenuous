package CreepTenuous.Api.Files.DeleteFile;

import CreepTenuous.Api.Files.DeleteFile.data.DataDeleteFile;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/file")
@RestController
public class DeleteFile {
    @DeleteMapping("/delete")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void deleteFile(@RequestBody DataDeleteFile file) {

    }
}
