package com.zer0s2m.CreepTenuous.api.controllers.directory.copy;

import com.zer0s2m.CreepTenuous.api.controllers.directory.copy.data.FormCopyDirectoryApi;
import com.zer0s2m.CreepTenuous.api.controllers.directory.copy.http.ResponseCopyDirectory;
import com.zer0s2m.CreepTenuous.api.core.annotations.V1APIRestController;
import com.zer0s2m.CreepTenuous.providers.redis.controllers.CheckRightsActionFileSystem;
import com.zer0s2m.CreepTenuous.services.directory.copy.services.impl.ServiceCopyDirectory;
import com.zer0s2m.CreepTenuous.providers.build.os.services.CheckIsExistsDirectoryApi;
import com.zer0s2m.CreepTenuous.services.directory.copy.services.impl.ServiceCopyDirectoryRedis;
import com.zer0s2m.CreepTenuous.utils.CloneList;
import com.zer0s2m.CreepTenuous.utils.containers.ContainerInfoFileSystemObject;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.IOException;
import java.util.List;

@V1APIRestController
public class ControllerApiCopyDirectory implements CheckIsExistsDirectoryApi, CheckRightsActionFileSystem {
    private final ServiceCopyDirectory serviceCopyDirectory;

    private final ServiceCopyDirectoryRedis serviceCopyDirectoryRedis;

    @Autowired
    public ControllerApiCopyDirectory(
            ServiceCopyDirectory serviceCopyDirectory,
            ServiceCopyDirectoryRedis serviceCopyDirectoryRedis
    ) {
        this.serviceCopyDirectory = serviceCopyDirectory;
        this.serviceCopyDirectoryRedis = serviceCopyDirectoryRedis;
    }

    @PostMapping("/directory/copy")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseCopyDirectory copy(
            final @Valid @RequestBody FormCopyDirectoryApi dataDirectory,
            @RequestHeader(name = "Authorization") String accessToken
    ) throws IOException {
        serviceCopyDirectoryRedis.setAccessToken(accessToken);
        serviceCopyDirectoryRedis.setEnableCheckIsNameDirectory(true);
        List<String> mergeParents = CloneList.cloneOneLevel(
                dataDirectory.systemParents(),
                dataDirectory.systemToParents()
        );
        serviceCopyDirectoryRedis.checkRights(
                dataDirectory.parents(),
                mergeParents,
                dataDirectory.systemNameDirectory()
        );

        List<ContainerInfoFileSystemObject> attached = serviceCopyDirectory.copy(
                dataDirectory.systemParents(),
                dataDirectory.systemToParents(),
                dataDirectory.systemNameDirectory(),
                dataDirectory.method()
        );
        serviceCopyDirectoryRedis.copy(attached);

        return new ResponseCopyDirectory(attached);
    }
}
