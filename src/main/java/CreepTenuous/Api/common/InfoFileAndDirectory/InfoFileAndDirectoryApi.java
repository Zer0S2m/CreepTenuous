package CreepTenuous.Api.common.InfoFileAndDirectory;

import CreepTenuous.Api.common.InfoFileAndDirectory.data.DataInfoAndDirectoryApi;
import CreepTenuous.Directory.check.CheckIsExistsDirectory;
import CreepTenuous.common.InfoFileAndDirectory.services.InfoFileAndDirectory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.nio.file.NoSuchFileException;

@RestController
@RequestMapping("/info-file-directory")
public class InfoFileAndDirectoryApi implements CheckIsExistsDirectory {
    @Autowired
    private InfoFileAndDirectory collectInfo;

    @GetMapping("")
    @ResponseStatus(code = HttpStatus.OK)
    public DataInfoAndDirectoryApi getInfo(
            @RequestParam(value = "parents", defaultValue = "[]") String[] parents
    ) throws NoSuchFileException {
        return collectInfo.collect(parents);
    }
}
