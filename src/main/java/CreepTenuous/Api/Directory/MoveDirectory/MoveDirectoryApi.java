package CreepTenuous.Api.Directory.MoveDirectory;

import CreepTenuous.Api.Directory.MoveDirectory.forms.FormMoveDirectoryApi;
import CreepTenuous.Directory.MoveDirectory.services.impl.MoveDirectory;

import CreepTenuous.Directory.utils.check.CheckIsExistsDirectoryApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.file.NoSuchFileException;

@RestController
@RequestMapping("/directory")
public class MoveDirectoryApi implements CheckIsExistsDirectoryApi {
    @Autowired
    private MoveDirectory moveDirectory;

    @PostMapping("/move")
    @ResponseStatus(code = HttpStatus.OK)
    public final void move(
            @RequestBody FormMoveDirectoryApi dataDirectory
    ) throws IOException {
        moveDirectory.move(
                dataDirectory.getParents(),
                dataDirectory.getToParents(),
                dataDirectory.getNameDirectory()
        );
    }
}
