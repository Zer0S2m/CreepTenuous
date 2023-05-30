package com.zer0s2m.creeptenuous.api.documentation.controllers;

import com.zer0s2m.creeptenuous.common.data.DataDeleteFileApi;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

import java.lang.reflect.InvocationTargetException;

public interface ControllerApiDeleteFileDoc {

    /**
     * Delete file
     * @param file file delete data
     * @param accessToken raw JWT access token
     * @throws InvocationTargetException Exception thrown by an invoked method or constructor.
     * @throws NoSuchMethodException Thrown when a particular method cannot be found.
     * @throws InstantiationException Thrown when an application tries to create an instance of a class
     * using the newInstance method in class {@code Class}.
     * @throws IllegalAccessException An IllegalAccessException is thrown when an application
     * tries to reflectively create an instance
     */
    @Operation(
            method = "DELETE",
            summary = "Deleting a file",
            description = "Deleting a file",
            tags = { "File" },
            security = @SecurityRequirement(name = "Bearer Authentication"),
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Data to delete a file",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = DataDeleteFileApi.class)
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "204",
                            description = "Successful file deleting",
                            content = @Content(
                                    schema = @Schema
                            )
                    ),
                    @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
                    @ApiResponse(responseCode = "403", ref = "#/components/responses/Forbidden"),
                    @ApiResponse(responseCode = "404", ref = "#/components/responses/NotFoundFileObjectSystem")
            }
    )
    void deleteFile(
            final DataDeleteFileApi file,
            @Parameter(hidden = true) String accessToken
    ) throws InvocationTargetException, NoSuchMethodException,
            InstantiationException, IllegalAccessException;
}
