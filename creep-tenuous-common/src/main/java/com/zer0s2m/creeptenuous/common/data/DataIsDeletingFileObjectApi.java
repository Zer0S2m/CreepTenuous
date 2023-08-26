package com.zer0s2m.creeptenuous.common.data;

import io.swagger.v3.oas.annotations.media.Schema;

public record DataIsDeletingFileObjectApi(

        @Schema(description = "Responsible for deleting the user's file objects if it is deleted")
        boolean isDelete

) { }
