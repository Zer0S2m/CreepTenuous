package com.zer0s2m.creeptenuous.common.http;

import com.zer0s2m.creeptenuous.common.containers.ContainerGrantedRight;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

public record ResponseGrantedRightsApi(

        @Schema(description = "The system name of the object in the file system")
        String systemName,

        @Schema(description = "The real name of the object in the file system")
        String realName,

        @Schema(description = "Rights granted to users")
        List<ContainerGrantedRight> rights

) { }
