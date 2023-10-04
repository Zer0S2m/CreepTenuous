package com.zer0s2m.creeptenuous.common.data;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record DataManagerInfoApi(

        @NotNull(message = "Please indicate the system names of file objects (Not NULL)")
        @Schema(description = "System names of file objects")
        List<String> systemNames

) { }
