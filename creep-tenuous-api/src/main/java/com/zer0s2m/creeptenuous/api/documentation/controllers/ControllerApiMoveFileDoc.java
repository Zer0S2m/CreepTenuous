package com.zer0s2m.creeptenuous.api.documentation.controllers;

import com.zer0s2m.creeptenuous.common.data.DataMoveFileApi;
import com.zer0s2m.creeptenuous.common.exceptions.FileObjectIsFrozenException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

public interface ControllerApiMoveFileDoc {

    /**
     * Moving files.
     *
     * @param file        File move data.
     * @param accessToken Raw JWT access token
     * @throws InvocationTargetException   Exception thrown by an invoked method or constructor.
     * @throws NoSuchMethodException       Thrown when a particular method cannot be found.
     * @throws InstantiationException      Thrown when an application tries to create an instance of a class
     *                                     using the newInstance method in class {@code Class}.
     * @throws IllegalAccessException      An IllegalAccessException is thrown when an application
     *                                     tries to reflectively create an instance.
     * @throws FileObjectIsFrozenException File object is frozen.
     * @throws IOException                 Signals that an I/O exception to some sort has occurred.
     */
    @Operation(
            method = "PUT",
            summary = "Moving a file(s)",
            description = "Moving a file and assigning rights to a user",
            tags = {"File"},
            security = @SecurityRequirement(name = "Bearer Authentication"),
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Data to move a file",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = DataMoveFileApi.class),
                            examples = {
                                    @ExampleObject(
                                            name = "MultiMove",
                                            description = "Moving more than one file",
                                            value = "{" +
                                                    "\"parents\": [\"string\"], " +
                                                    "\"systemParents\": [\"string\"], " +
                                                    "\"toParents\": [\"string\"], " +
                                                    "\"systemToParents\": [\"string\"], " +
                                                    "\"nameFiles\": [\"string\"]," +
                                                    "\"systemNameFiles\": [\"string\"]}"
                                    ),
                                    @ExampleObject(
                                            name = "SingleMove",
                                            description = "Moving a single file",
                                            value = "{" +
                                                    "\"parents\": [\"string\"], " +
                                                    "\"systemParents\": [\"string\"], " +
                                                    "\"toParents\": [\"string\"], " +
                                                    "\"systemToParents\": [\"string\"], " +
                                                    "\"fileName\": \"string\"," +
                                                    "\"systemFileName\": \"string\"}"
                                    ),
                            }
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "204",
                            description = "Successful file(s) moving"
                    ),
                    @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
                    @ApiResponse(responseCode = "403", ref = "#/components/responses/Forbidden"),
                    @ApiResponse(responseCode = "404", ref = "#/components/responses/NotFoundFileObjectSystem")
            }
    )
    void move(
            final DataMoveFileApi file,
            @Parameter(hidden = true) String accessToken
    ) throws InvocationTargetException, NoSuchMethodException,
            InstantiationException, IllegalAccessException, FileObjectIsFrozenException, IOException;

}
