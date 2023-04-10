package com.zer0s2m.CreepTenuous.api.controllers.common.infoFileAndDirectory;

import com.zer0s2m.CreepTenuous.api.controllers.common.infoFileAndDirectory.data.DataInfoAndDirectoryApi;
import com.zer0s2m.CreepTenuous.api.core.version.v1.V1APIController;
import com.zer0s2m.CreepTenuous.providers.build.os.services.CheckIsExistsDirectoryApi;
import com.zer0s2m.CreepTenuous.services.common.infoFileAndDirectory.impl.InfoFileAndDirectory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.nio.file.NoSuchFileException;
import java.util.List;

@V1APIController
public class InfoFileAndDirectoryApi implements CheckIsExistsDirectoryApi {
    private final InfoFileAndDirectory collectInfo;

    @Autowired
    public InfoFileAndDirectoryApi(InfoFileAndDirectory collectInfo) {
        this.collectInfo = collectInfo;
    }

    @GetMapping("/info-file-directory")
    @ResponseStatus(code = HttpStatus.OK)
    public DataInfoAndDirectoryApi getInfo(
            final @RequestParam(value = "parents") List<String> parents
    ) throws NoSuchFileException {
        return collectInfo.collect(parents);
    }
}
