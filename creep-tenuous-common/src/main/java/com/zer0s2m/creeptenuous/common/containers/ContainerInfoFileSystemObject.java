package com.zer0s2m.creeptenuous.common.containers;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;

import java.nio.file.Path;

public record ContainerInfoFileSystemObject(
        @JsonIgnore
        Path source,

        @JsonIgnore
        Path target,

        @Schema(description = "The system name of the file system object")
        String nameFileSystemObject,

        @Schema(description = "Whether the file system object is a file")
        Boolean isFile,

        @Schema(description = "Whether the filesystem object is a directory")
        Boolean isDirectory
) { }
