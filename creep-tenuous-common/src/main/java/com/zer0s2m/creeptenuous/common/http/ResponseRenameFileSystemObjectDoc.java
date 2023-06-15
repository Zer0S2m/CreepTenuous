package com.zer0s2m.creeptenuous.common.http;

import io.swagger.v3.oas.annotations.media.Schema;

public record ResponseRenameFileSystemObjectDoc(

        @Schema(description = "System file name")
        String systemName,

        @Schema(description = "New file name")
        String newRealName

) {
}
