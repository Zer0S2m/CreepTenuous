package com.zer0s2m.creeptenuous.api.documentation.controllers;

import com.zer0s2m.creeptenuous.common.data.DataCreateDirectoryApi;
import com.zer0s2m.creeptenuous.common.exceptions.ExistsFileSystemObjectRedisException;
import com.zer0s2m.creeptenuous.common.exceptions.FileAlreadyExistsException;
import com.zer0s2m.creeptenuous.common.exceptions.FileObjectIsFrozenException;
import com.zer0s2m.creeptenuous.common.http.ResponseCreateDirectoryApi;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

public interface ControllerApiCreateDirectoryDoc {

    /**
     * Creating a directory. Supports atomic file system mode.
     *
     * @param directoryForm Directory create data.
     * @param accessToken   Raw JWT access token.
     * @return Result create directory.
     * @throws FileAlreadyExistsException           file already exists.
     * @throws InvocationTargetException            Exception thrown by an invoked method or constructor.
     * @throws NoSuchMethodException                Thrown when a particular method cannot be found.
     * @throws InstantiationException               Thrown when an application tries to create an instance of a class
     *                                              using the newInstance method in class {@code Class}.
     * @throws IllegalAccessException               An IllegalAccessException is thrown when an application
     *                                              tries to reflectively create an instance.
     * @throws IOException                          Signals that an I/O exception to some sort has occurred.
     * @throws ExistsFileSystemObjectRedisException Uniqueness of the name in the system under different directory
     *                                              levels.
     * @throws FileObjectIsFrozenException          File object is frozen.
     */
    @Operation(
            method = "POST",
            summary = "Creating a directory",
            description = "Creating a directory and assigning rights to a user",
            tags = {"Directory"},
            security = @SecurityRequirement(name = "Bearer Authentication"),
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Data to create a directory",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = DataCreateDirectoryApi.class)
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "Successful directory creation",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ResponseCreateDirectoryApi.class)
                            )
                    ),
                    @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
                    @ApiResponse(responseCode = "403", ref = "#/components/responses/Forbidden"),
                    @ApiResponse(responseCode = "404", ref = "#/components/responses/NotFoundDirectory")
            }
    )
    ResponseCreateDirectoryApi createDirectory(
            final DataCreateDirectoryApi directoryForm,
            @Parameter(hidden = true) String accessToken)
            throws FileAlreadyExistsException, InvocationTargetException, NoSuchMethodException,
            InstantiationException, IllegalAccessException, IOException, ExistsFileSystemObjectRedisException,
            FileObjectIsFrozenException;

}
