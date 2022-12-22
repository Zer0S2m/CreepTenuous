package CreepTenuous.api.controllers.common.infoFileAndDirectory;

import CreepTenuous.api.controllers.common.infoFileAndDirectory.data.DataInfoAndDirectoryApi;
import CreepTenuous.api.core.version.v1.V1APIController;
import CreepTenuous.services.directory.utils.check.CheckIsExistsDirectoryApi;
import CreepTenuous.services.common.infoFileAndDirectory.services.InfoFileAndDirectory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.nio.file.NoSuchFileException;
import java.util.List;

@V1APIController
public class InfoFileAndDirectoryApi implements CheckIsExistsDirectoryApi {
    @Autowired
    private InfoFileAndDirectory collectInfo;

    @GetMapping("/info-file-directory")
    @ResponseStatus(code = HttpStatus.OK)
    public DataInfoAndDirectoryApi getInfo(
            @RequestParam(value = "parents") List<String> parents
    ) throws NoSuchFileException {
        return collectInfo.collect(parents);
    }
}
