package com.zer0s2m.CreepTenuous.api.controllers.files.copy;

import com.zer0s2m.CreepTenuous.api.controllers.files.copy.data.DataCopyFile;
import com.zer0s2m.CreepTenuous.api.core.annotations.V1APIRestController;
import com.zer0s2m.CreepTenuous.providers.build.os.services.CheckIsExistsDirectoryApi;
import com.zer0s2m.CreepTenuous.providers.build.os.services.CheckIsExistsFileApi;
import com.zer0s2m.CreepTenuous.services.files.copy.services.impl.ServiceCopyFile;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.IOException;
import java.util.Objects;

@V1APIRestController
public class ControllerApiCopyFile implements CheckIsExistsDirectoryApi, CheckIsExistsFileApi {
    private final ServiceCopyFile serviceCopyFile;

    @Autowired
    public ControllerApiCopyFile(ServiceCopyFile serviceCopyFile) {
        this.serviceCopyFile = serviceCopyFile;
    }

    @PostMapping("/file/copy")
    @ResponseStatus(code = HttpStatus.CREATED)
    public void createFile(
            final @Valid @RequestBody DataCopyFile file
    ) throws IOException {
        if (file.nameFile() != null) {
            serviceCopyFile.copy(
                    file.nameFile(),
                    file.parents(),
                    file.toParents()
            );
        } else {
            serviceCopyFile.copy(
                    Objects.requireNonNull(file.nameFiles()),
                    file.parents(),
                    file.toParents()
            );
        }
    }
}
