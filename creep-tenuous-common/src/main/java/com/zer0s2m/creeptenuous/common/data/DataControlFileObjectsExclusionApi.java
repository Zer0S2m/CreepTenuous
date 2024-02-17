package com.zer0s2m.creeptenuous.common.data;

import com.zer0s2m.creeptenuous.validation.constraints.UUIDCollectionValidator;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.Collection;

public record DataControlFileObjectsExclusionApi(

        @NotNull(message = "Please provide system names of file objects (Not NULL)")
        @NotEmpty(message = "Please provide system names of file objects")
        @UUIDCollectionValidator
        @Schema(description = "System names of file objects")
        Collection<String> fileObjects

) {
}
