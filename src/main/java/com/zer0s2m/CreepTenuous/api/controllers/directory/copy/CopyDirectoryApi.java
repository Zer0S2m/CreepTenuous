package com.zer0s2m.CreepTenuous.api.controllers.directory.copy;

import com.zer0s2m.CreepTenuous.api.controllers.directory.copy.data.FormCopyDirectoryApi;
import com.zer0s2m.CreepTenuous.api.core.version.v1.V1APIController;
import com.zer0s2m.CreepTenuous.services.directory.copy.services.impl.CopyDirectory;
import com.zer0s2m.CreepTenuous.providers.build.os.services.CheckIsExistsDirectoryApi;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.IOException;

@V1APIController
public class CopyDirectoryApi implements CheckIsExistsDirectoryApi {
    private final CopyDirectory copyDirectory;

    @PostMapping("/directory/copy")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void copy(
            final @RequestBody FormCopyDirectoryApi dataDirectory
    ) throws IOException {
        copyDirectory.copy(
                dataDirectory.parents(),
                dataDirectory.toParents(),
                dataDirectory.nameDirectory()
        );
    }

    @Autowired
    public CopyDirectoryApi(CopyDirectory copyDirectory) {
        this.copyDirectory = copyDirectory;
    }
}
