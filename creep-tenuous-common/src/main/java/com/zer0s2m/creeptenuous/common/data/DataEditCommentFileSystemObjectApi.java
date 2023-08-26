package com.zer0s2m.creeptenuous.common.data;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record DataEditCommentFileSystemObjectApi(

        @NotNull(message = "Please provide id (Not NULL)")
        @Min(value = 1, message = "Please enter more than zero")
        @Schema(description = "Id comment file object")
        Long id,

        @NotNull(message = "Please provide comment (Not NULL)")
        @NotBlank(message = "Please provide comment")
        @Schema(description = "Comment for file object")
        String comment

) { }
