package CreepTenuous.api.controllers.directory.copy;

import CreepTenuous.api.controllers.directory.copy.forms.FormCopyDirectoryApi;
import CreepTenuous.api.core.version.v1.V1APIController;
import CreepTenuous.services.directory.copy.services.impl.CopyDirectory;
import CreepTenuous.services.directory.utils.check.CheckIsExistsDirectoryApi;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.IOException;

@V1APIController
public class CopyDirectoryApi implements CheckIsExistsDirectoryApi {
    @Autowired
    private CopyDirectory copyDirectory;

    @PostMapping("/directory/copy")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void copy(
            @RequestBody FormCopyDirectoryApi dataDirectory
    ) throws IOException {
        copyDirectory.copy(
                dataDirectory.getParents(),
                dataDirectory.getToParents(),
                dataDirectory.getNameDirectory()
        );
    }
}
