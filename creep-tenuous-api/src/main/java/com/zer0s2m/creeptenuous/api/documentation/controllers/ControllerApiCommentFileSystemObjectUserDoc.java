package com.zer0s2m.creeptenuous.api.documentation.controllers;

import com.zer0s2m.creeptenuous.common.data.DataCreateCommentFileSystemObjectApi;
import com.zer0s2m.creeptenuous.common.data.DataDeleteCommentFileSystemObjectApi;
import com.zer0s2m.creeptenuous.models.common.CommentFileSystemObject;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

public interface ControllerApiCommentFileSystemObjectUserDoc {

    /**
     * Create a comment for a file object
     * @param data data to created
     * @param accessToken access JWT token
     * @return comment
     */
    @Operation(
            method = "POST",
            summary = "Create comment",
            description = "Create Comment for File System Object",
            tags = { "Common" },
            security = @SecurityRequirement(name = "Bearer Authentication"),
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "Successful comment created",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(example = "{" +
                                            "\"id\": 3," +
                                            "\"comment\": \"Comment\"," +
                                            "\"fileSystemObject\": \"73a21b4b-b513-46ee-ad9c-623105829ba9\"\n" +
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
            final @Parameter(hidden = true) String accessToken);

    @Operation(
            method = "DELETE",
            summary = "Delete comment",
            description = "Delete Comment for File System Object",
            tags = { "Common" },
            security = @SecurityRequirement(name = "Bearer Authentication"),
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
    void delete(final DataDeleteCommentFileSystemObjectApi data);

    void edit();

}
