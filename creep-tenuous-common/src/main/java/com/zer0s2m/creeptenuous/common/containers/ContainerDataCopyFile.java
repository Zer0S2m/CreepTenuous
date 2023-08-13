package com.zer0s2m.creeptenuous.common.containers;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;

import java.nio.file.Path;

public record ContainerDataCopyFile(

        @JsonIgnore
        Path target,

        @Schema(description = "System file name")
        String systemFileName

) { }
