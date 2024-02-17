package com.zer0s2m.creeptenuous.integration.implants;

import io.swagger.v3.oas.annotations.media.Schema;

public record ResponseStatisticsApi(

        @Schema(description = "Login of the user to whom the object belonged")
        String userLogin,

        @Schema(description = "System object name")
        String systemName,

        @Schema(description = "System path to the file object")
        String systemPath,

        @Schema(description = "Type of object to be deleted")
        String typeObjectDeleted,

        @Schema(description = "Statistics creation date")
        String createdAt

) { }
