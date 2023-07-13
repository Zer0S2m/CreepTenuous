package com.zer0s2m.creeptenuous.common.containers;

import com.zer0s2m.creeptenuous.common.enums.OperationRights;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

public record ContainerGrantedRight(

        @Schema(description = "username for which the rights are granted")
        String user,

        @Schema(description = "Rights")
        List<OperationRights> rights

) {
}
