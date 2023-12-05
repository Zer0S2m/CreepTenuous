package com.zer0s2m.creeptenuous.module.search;

import com.zer0s2m.creeptenuous.common.validation.constraints.EnumValidator;
import com.zer0s2m.creeptenuous.common.validation.constraints.UUIDCollectionValidator;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.Collection;

public record DataSearchFileObject(

        @NotNull(message = "Please provide source directory (system) (Not NULL)")
        @UUIDCollectionValidator
        @Schema(description = "Parts of real system paths (directories)")
        Collection<String> systemParents,

        @NotNull(message = "Please indicate part of the file object name (Not NULL)")
        @NotBlank(message = "Please indicate part of the file object name")
        @Schema(description = "Part of the real name of the file object")
        String partRealName,

        @EnumValidator(enumClass = SearchFileObject.class, message = "Please specify the file object type")
        @Schema(description = "File search type", allowableValues = {"DIRECTORY", "FILE", "ALL"})
        String type

) { }
