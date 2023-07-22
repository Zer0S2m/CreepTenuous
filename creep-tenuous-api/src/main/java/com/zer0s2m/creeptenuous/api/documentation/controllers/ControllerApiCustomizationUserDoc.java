package com.zer0s2m.creeptenuous.api.documentation.controllers;

import com.zer0s2m.creeptenuous.common.data.DataControlFileSystemObjectApi;
import com.zer0s2m.creeptenuous.common.data.DataCreateUserColorDirectoryApi;
import com.zer0s2m.creeptenuous.common.exceptions.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

public interface ControllerApiCustomizationUserDoc {

    /**
     * Set color for directory
     * @param data data to create
     * @param accessToken raw access JWT token
     * @throws UserNotFoundException user not found
     * @throws NoExistsFileSystemObjectRedisException the file system object was not found in the database
     * @throws FileObjectIsNotDirectoryTypeException file object is not a directory type
     */
    @Operation(
            method = "PUT",
            summary = "Set color from directory",
            description = "Set color from custom directory",
            tags = { "Customization" },
            security = @SecurityRequirement(name = "Bearer Authentication"),
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Data to created",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = DataCreateUserColorDirectoryApi.class)
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "204",
                            description = "Successful set",
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
                                            @ExampleObject(name = "Not found user", value ="{" +
                                                    "\"message\": \"Not found user\"," +
                                                    "\"statusCode\": 404" +
                                                    "}"
                                            ),
                                            @ExampleObject(name = "Not found system object", value ="{" +
                                                    "\"message\": \"Not found file system object\"," +
                                                    "\"statusCode\": 404" +
                                                    "}"
                                            )
                                    })
                    )
            }
    )
    void setColorInDirectory(
            final DataCreateUserColorDirectoryApi data,
            @Parameter(hidden = true) String accessToken)
            throws NotFoundException, FileObjectIsNotDirectoryTypeException;

    /**
     * Remove color from custom directory
     * @param data data to delete
     * @param accessToken raw access JWT token
     * @throws NotFoundUserColorDirectoryException color scheme not found for user directory
     * @throws NoExistsFileSystemObjectRedisException the file system object was not found in the database
     * @throws FileObjectIsNotDirectoryTypeException file object is not a directory type
     */
    @Operation(
            method = "DELETE",
            summary = "Remove color from directory",
            description = "Remove color from custom directory",
            tags = { "Customization" },
            security = @SecurityRequirement(name = "Bearer Authentication"),
            responses = {
                    @ApiResponse(
                            responseCode = "204",
                            description = "Successful deleting",
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
                                            @ExampleObject(name = "Not found color in directory", value ="{" +
                                                    "\"message\": \"Color scheme not found for user directory\"," +
                                                    "\"statusCode\": 404" +
                                                    "}"
                                            ),
                                            @ExampleObject(name = "Not found system object", value ="{" +
                                                    "\"message\": \"Not found file system object\"," +
                                                    "\"statusCode\": 404" +
                                                    "}"
                                            )
                                    })
                    )
            }
    )
    void deleteColorInDirectory(
            final DataControlFileSystemObjectApi data, @Parameter(hidden = true) String accessToken)
            throws NotFoundException, FileObjectIsNotDirectoryTypeException;

}
