package com.zer0s2m.creeptenuous.api.documentation.controllers;

import com.zer0s2m.creeptenuous.common.data.DataCreateCommentFileSystemObjectApi;
import com.zer0s2m.creeptenuous.common.data.DataControlAnyObjectApi;
import com.zer0s2m.creeptenuous.common.data.DataEditCommentFileSystemObjectApi;
import com.zer0s2m.creeptenuous.common.exceptions.NotFoundException;
import com.zer0s2m.creeptenuous.models.common.CommentFileSystemObject;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

import java.util.List;

public interface ControllerApiCommentFileSystemObjectUserDoc {

    /**
     * Get all comments of a file object for a user
     * @param fileSystemObject name file system object
     * @param accessToken access JWT token
     * @return comments
     * @throws NotFoundException not found comments for filesystem objects
     */
    @Operation(
            method = "GET",
            summary = "Get comments",
            description = "Get all comment for File System Object",
            tags = { "Common" },
            security = @SecurityRequirement(name = "Bearer Authentication"),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successful get",
                            content = @Content(
                                    examples = {
                                            @ExampleObject(name = "Comments", value = "[\n" +
                                                    "{" +
                                                    "\"id\": 4," +
                                                    "\"comment\": \"Comment\"," +
                                                    "\"fileSystemObject\": \"8159f622-2dc3-4d56-a29d-e5c9cdfbb25c\"," +
                                                    "\"createdAt\": \"2023-10-14T12:30:20.583808\"" +
                                                    "}" +
                                                    "]"
                                            )
                                    }
                            )
                    ),
                    @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
                    @ApiResponse(responseCode = "403", ref = "#/components/responses/Forbidden"),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Not found",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = {
                                            @ExampleObject(name = "Not found comment", value ="{" +
                                                    "\"message\": \"Not found comment\"," +
                                                    "\"statusCode\": 404" +
                                                    "}"
                                            )
                                    })
                    )
            }
    )
    List<CommentFileSystemObject> list(
            final String fileSystemObject, final @Parameter(hidden = true) String accessToken)
            throws NotFoundException;

    /**
     * Create a comment for a file object
     * @param data data to created
     * @param accessToken access JWT token
     * @return comment
     * @throws NotFoundException not found comments for filesystem objects
     */
    @Operation(
            method = "POST",
            summary = "Create comment",
            description = "Create Comment for File System Object",
            tags = { "Common" },
            security = @SecurityRequirement(name = "Bearer Authentication"),
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Data to created",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = DataCreateCommentFileSystemObjectApi.class)
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "Successful comment created",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(example = "{" +
                                            "\"id\": 3," +
                                            "\"comment\": \"Comment\"," +
                                            "\"fileSystemObject\": \"73a21b4b-b513-46ee-ad9c-623105829ba9\"," +
                                            "\"createdAt\": \"2023-10-14T12:30:20.583808\"" +
                                            "}")
                            )
                    ),
                    @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
                    @ApiResponse(responseCode = "403", ref = "#/components/responses/Forbidden"),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Not found",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = {
                                            @ExampleObject(name = "Not found system object", value ="{" +
                                                    "\"message\": \"Not found file system object\"," +
                                                    "\"statusCode\": 404" +
                                                    "}"
                                            )
                                    })
                    )
            }
    )
    CommentFileSystemObject create(
            final DataCreateCommentFileSystemObjectApi data,
            final @Parameter(hidden = true) String accessToken) throws NotFoundException;

    /**
     * Delete a comment for a file object
     * @param data data to deleting
     * @param accessToken access JWT token
     * @throws NotFoundException not found comments for filesystem objects
     */
    @Operation(
            method = "DELETE",
            summary = "Delete comment",
            description = "Delete Comment for File System Object",
            tags = { "Common" },
            security = @SecurityRequirement(name = "Bearer Authentication"),
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Data to created",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = DataControlAnyObjectApi.class)
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "204",
                            description = "Successful comment deleted",
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
                                            @ExampleObject(name = "Not found comment", value ="{" +
                                                    "\"message\": \"Not found comment\"," +
                                                    "\"statusCode\": 404" +
                                                    "}"
                                            )
                                    })
                    )
            }
    )
    void delete(final DataControlAnyObjectApi data,
                final @Parameter(hidden = true) String accessToken) throws NotFoundException;

    /**
     * Edit a comment for a file object
     * @param data data to editing
     * @param accessToken access JWT token
     * @return comment
     * @throws NotFoundException not found comments for filesystem objects
     */
    @Operation(
            method = "PUT",
            summary = "Editing comment",
            description = "Editing Comment for File System Object",
            tags = { "Common" },
            security = @SecurityRequirement(name = "Bearer Authentication"),
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Data to editing",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = DataEditCommentFileSystemObjectApi.class)
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successful comment editing",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(example = "{" +
                                            "\"id\": 3," +
                                            "\"comment\": \"Comment\"," +
                                            "\"fileSystemObject\": \"73a21b4b-b513-46ee-ad9c-623105829ba9\"," +
                                            "\"createdAt\": \"2023-10-14T12:30:20.583808\"" +
                                            "}")
                            )
                    ),
                    @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
                    @ApiResponse(responseCode = "403", ref = "#/components/responses/Forbidden"),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Not found",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = {
                                            @ExampleObject(name = "Not found comment", value ="{" +
                                                    "\"message\": \"Not found comment\"," +
                                                    "\"statusCode\": 404" +
                                                    "}"
                                            ),
                                    })
                    )
            }
    )
    CommentFileSystemObject edit(
            final DataEditCommentFileSystemObjectApi data,
            final @Parameter(hidden = true) String accessToken)
            throws NotFoundException;

}
