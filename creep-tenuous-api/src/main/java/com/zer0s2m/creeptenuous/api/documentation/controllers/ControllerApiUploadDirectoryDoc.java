package com.zer0s2m.creeptenuous.api.documentation.controllers;

import com.zer0s2m.creeptenuous.common.exceptions.FileObjectIsFrozenException;
import com.zer0s2m.creeptenuous.common.http.ResponseUploadDirectoryApi;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

public interface ControllerApiUploadDirectoryDoc {

    /**
     * Uploading a directory (zip archive). Supports atomic file system mode.
     *
     * @param parents       Real names directories.
     * @param systemParents Parts of the system path - source.
     * @param zipFile       Raw zip archive.
     * @param accessToken   Raw JWT access token.
     * @return Result upload directory (zip archive).
     * @throws InvocationTargetException   Exception thrown by an invoked method or constructor.
     * @throws NoSuchMethodException       Thrown when a particular method cannot be found.
     * @throws InstantiationException      Thrown when an application tries to create an instance of a class
     *                                     using the newInstance method in class {@code Class}.
     * @throws IllegalAccessException      An IllegalAccessException is thrown when an application
     *                                     tries to reflectively create an instance.
     * @throws IOException                 Signals that an I/O exception to some sort has occurred.
     * @throws FileObjectIsFrozenException File object is frozen.
     */
    @Operation(
            method = "POST",
            summary = "Uploading a directory",
            description = "Uploading a directory - archive file",
            tags = {"Directory"},
            security = @SecurityRequirement(name = "Bearer Authentication"),
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Data to upload a directory",
                    content = @Content(
                            mediaType = "multipart/form-data",
                            schema = @Schema(description = "Archive file", type = "string", format = "binary")
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "Successful directory uploading",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ResponseUploadDirectoryApi.class)
                            )
                    ),
                    @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
                    @ApiResponse(responseCode = "403", ref = "#/components/responses/Forbidden"),
                    @ApiResponse(responseCode = "404", ref = "#/components/responses/NotFoundFileObjectSystem")
            }
    )
    ResponseUploadDirectoryApi upload(
            final @Schema(
                    description = "Parts of real paths (directories)",
                    type = "array"
            ) List<String> parents,
            final @Schema(
                    description = "Parts of system paths (directories)",
                    type = "array"
            ) List<String> systemParents,
            final MultipartFile zipFile,
            @Parameter(hidden = true) String accessToken
    ) throws InvocationTargetException, NoSuchMethodException, InstantiationException,
            IllegalAccessException, IOException, FileObjectIsFrozenException;
}
