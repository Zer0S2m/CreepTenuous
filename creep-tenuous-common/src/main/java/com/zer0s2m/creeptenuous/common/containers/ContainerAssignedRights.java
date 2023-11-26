package com.zer0s2m.creeptenuous.common.containers;

import com.zer0s2m.creeptenuous.common.enums.OperationRights;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

public record ContainerAssignedRights(

        @Schema(description = "The real name of the object in the file system")
        String realName,

        @Schema(description = "The system name of the object in the file system")
        String systemName,

        @Schema(description = "Rights")
        List<OperationRights> rights

) { }
