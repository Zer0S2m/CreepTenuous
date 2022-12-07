package CreepTenuous.Api.common.InfoFileAndDirectory;

import CreepTenuous.Api.common.InfoFileAndDirectory.data.DataInfoAndDirectoryApi;
import CreepTenuous.common.InfoFileAndDirectory.services.InfoFileAndDirectory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/info-file-directory")
public class InfoFileAndDirectoryApi {
    @Autowired
    private InfoFileAndDirectory collectInfo;

    @GetMapping("")
    @ResponseStatus(code = HttpStatus.OK)
    public DataInfoAndDirectoryApi getInfo(
            @RequestParam(value = "parents", defaultValue = "[]") String[] parents
    ) {
        return collectInfo.collect(parents);
    }
}
