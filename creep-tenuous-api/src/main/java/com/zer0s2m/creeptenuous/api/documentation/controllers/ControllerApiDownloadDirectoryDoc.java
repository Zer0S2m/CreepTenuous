package com.zer0s2m.creeptenuous.api.documentation.controllers;

import com.zer0s2m.creeptenuous.common.data.DataDownloadDirectoryApi;
import com.zer0s2m.creeptenuous.common.data.DataDownloadDirectorySelectApi;
import com.zer0s2m.creeptenuous.common.exceptions.FileObjectIsFrozenException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.ResponseEntity;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

public interface ControllerApiDownloadDirectoryDoc {

    /**
     * Downloading a directory. Supports atomic file system mode.
     *
     * @param data        Вirectory download data.
     * @param accessToken Кaw JWT access token.
     * @return Zip file.
     * @throws IOException                 Signals that an I/O exception to some sort has occurred.
     * @throws InvocationTargetException   Exception thrown by an invoked method or constructor.
     * @throws NoSuchMethodException       Thrown when a particular method cannot be found.
     * @throws InstantiationException      Thrown when an application tries to create an instance of a class
     *                                     using the newInstance method in class {@code Class}.
     * @throws IllegalAccessException      An IllegalAccessException is thrown when an application
     *                                     tries to reflectively create an instance.
     * @throws FileObjectIsFrozenException File object is frozen.
     */
    @Operation(
            method = "POST",
            summary = "Download directory",
            description = "Download a directory - output format: zip archive",
            tags = {"Directory"},
            security = @SecurityRequirement(name = "Bearer Authentication"),
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Data to download a directory",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = DataDownloadDirectoryApi.class)
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successful directory download",
                            content = @Content(
                                    mediaType = "application/zip",
                                    schema = @Schema(implementation = StreamingResponseBody.class)
                            )
                    ),
                    @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
                    @ApiResponse(responseCode = "403", ref = "#/components/responses/Forbidden"),
                    @ApiResponse(responseCode = "404", ref = "#/components/responses/NotFoundDirectory")
            }
    )
    ResponseEntity<StreamingResponseBody> download(
            final DataDownloadDirectoryApi data,
            @Parameter(hidden = true) String accessToken
    ) throws IOException, InvocationTargetException, NoSuchMethodException,
            InstantiationException, IllegalAccessException, FileObjectIsFrozenException;

    /**
     * Download directory with selected file objects. Supports atomic file system mode.
     *
     * @param data        Directory download data.
     * @param accessToken Raw JWT access token.
     * @return Zip file.
     * @throws IOException                 Signals that an I/O exception to some sort has occurred.
     * @throws InvocationTargetException   Exception thrown by an invoked method or constructor.
     * @throws NoSuchMethodException       Thrown when a particular method cannot be found.
     * @throws InstantiationException      Thrown when an application tries to create an instance of a class
     *                                     using the newInstance method in class {@code Class}.
     * @throws IllegalAccessException      An IllegalAccessException is thrown when an application
     *                                     tries to reflectively create an instance.
     * @throws FileObjectIsFrozenException File object is frozen.
     */
    @Operation(
            method = "POST",
            summary = "Download file system objects selectively",
            description = "Download file system objects selectively - output format: zip archive",
            tags = {"Directory"},
            security = @SecurityRequirement(name = "Bearer Authentication"),
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Data to download a file system objects selectively",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = DataDownloadDirectorySelectApi.class)
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successful file system objects selectively download",
                            content = @Content(
                                    mediaType = "application/zip",
                                    schema = @Schema(implementation = StreamingResponseBody.class)
                            )
                    ),
                    @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
                    @ApiResponse(responseCode = "403", ref = "#/components/responses/Forbidden"),
                    @ApiResponse(responseCode = "404", ref = "#/components/responses/NotFoundFileObjectSystem")
            }
    )
    ResponseEntity<StreamingResponseBody> downloadSelect(
            final DataDownloadDirectorySelectApi data,
            @Parameter(hidden = true) String accessToken
    ) throws IOException, InvocationTargetException, NoSuchMethodException,
            InstantiationException, IllegalAccessException, FileObjectIsFrozenException;

}
