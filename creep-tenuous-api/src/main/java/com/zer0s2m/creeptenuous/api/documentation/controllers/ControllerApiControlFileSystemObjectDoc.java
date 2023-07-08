package com.zer0s2m.creeptenuous.api.documentation.controllers;

import com.zer0s2m.creeptenuous.common.data.DataControlFileSystemObjectApi;
import com.zer0s2m.creeptenuous.common.exceptions.NotFoundException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

public interface ControllerApiControlFileSystemObjectDoc {

    /**
     * Freeze a file object by its name
     * @param data data to freeze the file object
     * @param accessToken access JWT token
     * @throws NotFoundException not found file system object
     */
    @Operation(
            method = "POST",
            summary = "Freeze file object",
            description = "Freeze a file object by its name. Denies all access to users who have rights",
            tags = { "Common" },
            security = @SecurityRequirement(name = "Bearer Authentication"),
            responses = {
                    @ApiResponse(
                            responseCode = "204",
                            description = "Successful freeze",
                            content = @Content
                    ),
                    @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
                    @ApiResponse(responseCode = "403", ref = "#/components/responses/Forbidden"),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Not found",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = {
                                            @ExampleObject(name = "Not found system object", value ="{" +
                                                    "\"message\": \"Not found file system object\"," +
                                                    "\"statusCode\": 404" +
                                                    "}"
                                            )
                                    })
                    )
            }
    )
    void freezingFileSystemObject(
            final DataControlFileSystemObjectApi data,
            final @Parameter(hidden = true) String accessToken) throws NotFoundException;

    /**
     * Unfreeze a file object by its name
     * @param data data to unfreeze a file object
     * @param accessToken access JWT token
     * @throws NotFoundException not found file system object
     */
    @Operation(
            method = "DELETE",
            summary = "Unfreeze file object",
            description = "Unfreeze a file object by its name. Allows any access to users with rights",
            tags = { "Common" },
            security = @SecurityRequirement(name = "Bearer Authentication"),
            responses = {
                    @ApiResponse(
                            responseCode = "204",
                            description = "Successful unfreeze",
                            content = @Content
                    ),
                    @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
                    @ApiResponse(responseCode = "403", ref = "#/components/responses/Forbidden"),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Not found",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = {
                                            @ExampleObject(name = "Not found system object", value ="{" +
                                                    "\"message\": \"Not found file system object\"," +
                                                    "\"statusCode\": 404" +
                                                    "}"
                                            )
                                    })
                    )
            }
    )
    void unfreezingFileSystemObject(
            final DataControlFileSystemObjectApi data,
            final @Parameter(hidden = true) String accessToken) throws NotFoundException;

}
