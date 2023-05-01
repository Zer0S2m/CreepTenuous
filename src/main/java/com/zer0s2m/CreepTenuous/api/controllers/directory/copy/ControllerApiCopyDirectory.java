package com.zer0s2m.CreepTenuous.api.controllers.directory.copy;

import com.zer0s2m.CreepTenuous.api.controllers.directory.copy.data.FormCopyDirectoryApi;
import com.zer0s2m.CreepTenuous.api.core.annotations.V1APIRestController;
import com.zer0s2m.CreepTenuous.services.directory.copy.services.impl.ServiceCopyDirectory;
import com.zer0s2m.CreepTenuous.providers.build.os.services.CheckIsExistsDirectoryApi;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.IOException;

@V1APIRestController
public class ControllerApiCopyDirectory implements CheckIsExistsDirectoryApi {
    private final ServiceCopyDirectory copyDirectory;

    @PostMapping("/directory/copy")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void copy(
            final @Valid @RequestBody FormCopyDirectoryApi dataDirectory
    ) throws IOException {
        copyDirectory.copy(
                dataDirectory.parents(),
                dataDirectory.toParents(),
                dataDirectory.nameDirectory(),
                dataDirectory.method()
        );
    }

    @Autowired
    public ControllerApiCopyDirectory(ServiceCopyDirectory copyDirectory) {
        this.copyDirectory = copyDirectory;
    }
}
