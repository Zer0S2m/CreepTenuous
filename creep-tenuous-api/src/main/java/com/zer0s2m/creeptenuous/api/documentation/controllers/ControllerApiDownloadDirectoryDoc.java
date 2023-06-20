package com.zer0s2m.creeptenuous.api.documentation.controllers;

import com.zer0s2m.creeptenuous.common.data.DataDownloadDirectoryApi;
import com.zer0s2m.creeptenuous.common.data.DataDownloadDirectorySelectApi;
import com.zer0s2m.creeptenuous.core.handlers.AtomicSystemCallManager;
import com.zer0s2m.creeptenuous.services.system.impl.ServiceDownloadDirectoryImpl;
import com.zer0s2m.creeptenuous.services.system.impl.ServiceDownloadDirectorySelectImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

public interface ControllerApiDownloadDirectoryDoc {

    /**
     * Download directory
     * <p>Called method via {@link AtomicSystemCallManager} - {@link ServiceDownloadDirectoryImpl#download(List, String)}</p>
     * @param data directory download data
     * @param accessToken raw JWT access token
     * @return zip file
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
            summary = "Download directory",
            description = "Download a directory - output format: zip archive",
            tags = { "Directory" },
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
                                    schema = @Schema(implementation = Resource.class)
                            )
                    ),
                    @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
                    @ApiResponse(responseCode = "403", ref = "#/components/responses/Forbidden"),
                    @ApiResponse(responseCode = "404", ref = "#/components/responses/NotFoundDirectory")
            }
    )
    ResponseEntity<Resource> download(
            final DataDownloadDirectoryApi data,
            @Parameter(hidden = true) String accessToken
    ) throws IOException, InvocationTargetException, NoSuchMethodException,
            InstantiationException, IllegalAccessException;

    /**
     * Download directory with selected file objects
     * <p>Called method via {@link AtomicSystemCallManager} - {@link ServiceDownloadDirectorySelectImpl#download()}</p>
     *
     * @param data        directory download data
     * @param accessToken raw JWT access token
     * @return zip file
     * @throws IOException               if an I/O error occurs or the parent directory does not exist
     * @throws InvocationTargetException Exception thrown by an invoked method or constructor.
     * @throws NoSuchMethodException     Thrown when a particular method cannot be found.
     * @throws InstantiationException    Thrown when an application tries to create an instance of a class
     *                                   using the newInstance method in class {@code Class}.
     * @throws IllegalAccessException    An IllegalAccessException is thrown when an application
     *                                   tries to reflectively create an instance
     */
    @Operation(
            method = "POST",
            summary = "Download file system objects selectively",
            description = "Download file system objects selectively - output format: zip archive",
            tags = { "Directory" },
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
                                    schema = @Schema(implementation = Resource.class)
                            )
                    ),
                    @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
                    @ApiResponse(responseCode = "403", ref = "#/components/responses/Forbidden"),
                    @ApiResponse(responseCode = "404", ref = "#/components/responses/NotFoundFileObjectSystem")
            }
    )
    ResponseEntity<Resource> downloadSelect(
            final DataDownloadDirectorySelectApi data,
            @Parameter(hidden = true) String accessToken
    ) throws IOException, InvocationTargetException, NoSuchMethodException,
            InstantiationException, IllegalAccessException;
}
