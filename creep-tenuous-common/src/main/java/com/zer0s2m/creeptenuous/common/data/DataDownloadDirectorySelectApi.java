package com.zer0s2m.creeptenuous.common.data;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record DataDownloadDirectorySelectApi(

        @NotNull(message = "Please provide selection of file objects (Not NULL)")
        @Schema(description = "Parts of selection of file objects (directories)")
        List<DataDownloadDirectorySelectPartApi> attached

) { }
