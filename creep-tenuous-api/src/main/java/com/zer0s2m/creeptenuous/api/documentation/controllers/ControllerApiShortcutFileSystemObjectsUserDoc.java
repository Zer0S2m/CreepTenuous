package com.zer0s2m.creeptenuous.api.documentation.controllers;

import com.zer0s2m.creeptenuous.common.data.DataControlShortcutFileSystemObjectApi;
import com.zer0s2m.creeptenuous.common.exceptions.NotFoundCommentFileSystemObjectException;
import com.zer0s2m.creeptenuous.common.exceptions.UserNotFoundException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

public interface ControllerApiShortcutFileSystemObjectsUserDoc {

    /**
     * Create a shortcut to a file object
     * @param data data to create
     * @param accessToken raw access JWT token
     * @throws UserNotFoundException user not exists
     * @throws NotFoundCommentFileSystemObjectException file object not exists
     */
    @Operation(
            method = "POST",
            summary = "Create a shortcut to a file object",
            description = "Create a shortcut to a file object for quick access to it",
            tags = { "Common" },
            security = @SecurityRequirement(name = "Bearer Authentication"),
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Data to created",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = DataControlShortcutFileSystemObjectApi.class)
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "204",
                            description = "Successful create",
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
    void create(
            final DataControlShortcutFileSystemObjectApi data,
            @Parameter(hidden = true) String accessToken) throws UserNotFoundException,
            NotFoundCommentFileSystemObjectException;

    /**
     * Delete a shortcut to a file object
     * @param data data to delete
     * @param accessToken raw access JWT token
     * @throws UserNotFoundException user not exists
     */
    @Operation(
            method = "DELETE",
            summary = "Delete a shortcut to a file object",
            description = "Delete a shortcut to a file object for quick access to it",
            tags = { "Common" },
            security = @SecurityRequirement(name = "Bearer Authentication"),
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Data to deleted",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = DataControlShortcutFileSystemObjectApi.class)
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "204",
                            description = "Successful delete",
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
    void delete(final DataControlShortcutFileSystemObjectApi data,
                @Parameter(hidden = true) String accessToken) throws UserNotFoundException;

}
