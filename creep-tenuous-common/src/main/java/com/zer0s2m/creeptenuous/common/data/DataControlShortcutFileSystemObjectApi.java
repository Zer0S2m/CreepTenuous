package com.zer0s2m.creeptenuous.common.data;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record DataControlShortcutFileSystemObjectApi(

        @NotNull(message = "Please provide a name for the attached file object (Not NULL)")
        @NotBlank(message = "Please provide a name for the attached file object")
        @Schema(description = "System file object name")
        String toAttachedFileSystemObject,

        @NotNull(message = "Please specify the name of the attached file object (Not NULL)")
        @NotBlank(message = "Please specify the name of the attached file object")
        @Schema(description = "System file object name")
        String attachedFileSystemObject

) {
}
