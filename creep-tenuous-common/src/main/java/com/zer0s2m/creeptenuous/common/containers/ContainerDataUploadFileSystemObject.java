package com.zer0s2m.creeptenuous.common.containers;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;

import java.nio.file.Path;

/**
 * Archive unpacking data. Includes information about <b>files</b> and <b>directories</b>
 * @param realName
 * @param systemName
 * @param systemPath
 * @param isFile
 * @param isDirectory
 */
public record ContainerDataUploadFileSystemObject(

        @Schema(description = "The system name of the object in the file system")
        String realName,

        @Schema(description = "The system name of the object in the file system")
        String systemName,

        @JsonIgnore
        Path systemPath,

        @Schema(description = "Whether the file system object is a file")
        Boolean isFile,

        @Schema(description = "Whether the filesystem object is a directory")
        Boolean isDirectory

) { }
