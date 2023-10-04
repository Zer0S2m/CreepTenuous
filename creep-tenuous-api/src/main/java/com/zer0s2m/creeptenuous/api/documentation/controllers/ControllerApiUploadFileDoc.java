package com.zer0s2m.creeptenuous.api.documentation.controllers;

import com.zer0s2m.creeptenuous.common.exceptions.FileObjectIsFrozenException;
import com.zer0s2m.creeptenuous.common.http.ResponseUploadFileApi;
import com.zer0s2m.creeptenuous.core.atomic.handlers.AtomicSystemCallManager;
import com.zer0s2m.creeptenuous.services.system.impl.ServiceUploadFileImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.web.multipart.MultipartFile;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

public interface ControllerApiUploadFileDoc {

    /**
     * Upload file
     * <p>Called method via {@link AtomicSystemCallManager} - {@link ServiceUploadFileImpl#upload(List, List)}</p>
     *
     * @param files         raw files
     * @param parents       real names directories
     * @param systemParents parts of the system path - source
     * @param accessToken   raw JWT access token
     * @return result upload file
     * @throws InvocationTargetException   Exception thrown by an invoked method or constructor.
     * @throws NoSuchMethodException       Thrown when a particular method cannot be found.
     * @throws InstantiationException      Thrown when an application tries to create an instance of a class
     *                                     using the newInstance method in class {@code Class}.
     * @throws IllegalAccessException      An IllegalAccessException is thrown when an application
     *                                     tries to reflectively create an instance
     * @throws FileObjectIsFrozenException file object is frozen
     */
    @Operation(
            method = "POST",
            summary = "Uploading a file(s)",
            description = "Uploading a file(s)",
            tags = {"File"},
            security = @SecurityRequirement(name = "Bearer Authentication"),
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Data to upload a file(s)",
                    content = @Content(
                            mediaType = "multipart/form-data",
                            schema = @Schema(description = "file", type = "string", format = "binary")
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "Successful file(s) uploading",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ResponseUploadFileApi.class)
                            )
                    ),
                    @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
                    @ApiResponse(responseCode = "403", ref = "#/components/responses/Forbidden"),
                    @ApiResponse(responseCode = "404", ref = "#/components/responses/NotFoundFileObjectSystem")
            }
    )
    ResponseUploadFileApi upload(
            final List<MultipartFile> files,
            final @Schema(
                    description = "Parts of real paths (directories)",
                    type = "array"
            ) List<String> parents,
            final @Schema(
                    description = "Parts of system paths (directories)",
                    type = "array"
            ) List<String> systemParents,
            @Parameter(hidden = true) String accessToken
    ) throws InvocationTargetException, NoSuchMethodException,
            InstantiationException, IllegalAccessException, FileObjectIsFrozenException;
}
