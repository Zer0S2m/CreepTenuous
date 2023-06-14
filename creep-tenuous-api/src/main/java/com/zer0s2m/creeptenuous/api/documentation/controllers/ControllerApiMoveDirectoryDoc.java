package com.zer0s2m.creeptenuous.api.documentation.controllers;

import com.zer0s2m.creeptenuous.common.data.DataMoveDirectoryApi;
import com.zer0s2m.creeptenuous.core.handlers.AtomicSystemCallManager;
import com.zer0s2m.creeptenuous.services.system.impl.ServiceMoveDirectoryImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

public interface ControllerApiMoveDirectoryDoc {

    /**
     * Move directory
     * <p>Called method via {@link AtomicSystemCallManager} - {@link ServiceMoveDirectoryImpl#move(List, List, String, Integer)}</p>
     *
     * @param dataDirectory directory move data
     * @param accessToken   raw JWT access token
     * @throws InvocationTargetException Exception thrown by an invoked method or constructor.
     * @throws NoSuchMethodException     Thrown when a particular method cannot be found.
     * @throws InstantiationException    Thrown when an application tries to create an instance of a class
     *                                   using the newInstance method in class {@code Class}.
     * @throws IllegalAccessException    An IllegalAccessException is thrown when an application
     *                                   tries to reflectively create an instance
     * @throws IOException               signals that an I/O exception of some sort has occurred
     */
    @Operation(
            method = "PUT",
            summary = "Moving a directory",
            description = "Moving a directory and assigning rights to a user",
            tags = { "Directory" },
            security = @SecurityRequirement(name = "Bearer Authentication"),
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Moving to copy a directory",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = DataMoveDirectoryApi.class)
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "204",
                            description = "Successful moving coping"
                    ),
                    @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
                    @ApiResponse(responseCode = "403", ref = "#/components/responses/Forbidden"),
                    @ApiResponse(responseCode = "404", ref = "#/components/responses/NotFoundDirectory")
            }
    )
    void move(
            final DataMoveDirectoryApi dataDirectory,
            @Parameter(hidden = true) String accessToken
    ) throws InvocationTargetException, NoSuchMethodException,
            InstantiationException, IllegalAccessException, IOException;
}
