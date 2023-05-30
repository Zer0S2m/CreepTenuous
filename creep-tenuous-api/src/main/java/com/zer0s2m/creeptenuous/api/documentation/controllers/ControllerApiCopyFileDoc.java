package com.zer0s2m.creeptenuous.api.documentation.controllers;

import com.zer0s2m.creeptenuous.common.data.DataCopyFileApi;
import com.zer0s2m.creeptenuous.common.http.ResponseCopyFileApi;
import com.zer0s2m.creeptenuous.core.handlers.AtomicSystemCallManager;
import com.zer0s2m.creeptenuous.services.system.impl.ServiceCopyFileImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

public interface ControllerApiCopyFileDoc {

    /**
     * Copy file(s)
     * <p>Called method via {@link AtomicSystemCallManager} - {@link ServiceCopyFileImpl#copy(String, List, List)}
     * or {@link ServiceCopyFileImpl#copy(List, List, List)}</p>
     * @param file copy data file
     * @param accessToken raw JWT access token
     * @return result copy file(s)
     * @throws IOException if an I/O error occurs or the parent directory does not exist
     * @throws InvocationTargetException Exception thrown by an invoked method or constructor.
     * @throws NoSuchMethodException Thrown when a particular method cannot be found.
     * @throws InstantiationException Thrown when an application tries to create an instance of a class
     * using the newInstance method in class {@code Class}.
     * @throws IllegalAccessException An IllegalAccessException is thrown when an application
     * tries to reflectively create an instance
     */
    @Operation(
            method = "POST",
            summary = "Coping a file",
            description = "Coping a file and assigning rights to a user",
            tags = { "File" },
            security = @SecurityRequirement(name = "Bearer Authentication"),
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Data to copy a file",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = DataCopyFileApi.class),
                            examples = {
                                    @ExampleObject(
                                            name = "MultiCopy",
                                            description = "Copying more than one file",
                                            value = "{" +
                                                    "\"parents\": [\"string\"], " +
                                                    "\"systemParents\": [\"string\"], " +
                                                    "\"toParents\": [\"string\"], " +
                                                    "\"systemToParents\": [\"string\"], " +
                                                    "\"nameFiles\": [\"string\"]," +
                                                    "\"systemNameFiles\": [\"string\"]}"
                                    ),
                                    @ExampleObject(
                                            name = "SingleCopy",
                                            description = "Copying a single file",
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
                            responseCode = "201",
                            description = "Successful directory coping",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ResponseCopyFileApi.class)
                            )
                    ),
                    @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
                    @ApiResponse(responseCode = "403", ref = "#/components/responses/Forbidden"),
                    @ApiResponse(responseCode = "404", ref = "#/components/responses/NotFoundFileObjectSystem")
            }
    )
    ResponseCopyFileApi copy(
            final DataCopyFileApi file,
            @Parameter(hidden = true) String accessToken
    ) throws IOException, InvocationTargetException, NoSuchMethodException,
            InstantiationException, IllegalAccessException;
}
