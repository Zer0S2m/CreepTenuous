package com.zer0s2m.creeptenuous.api.documentation.controllers;

import com.zer0s2m.creeptenuous.common.exceptions.UserAlreadyExistException;
import com.zer0s2m.creeptenuous.common.exceptions.messages.UserAlreadyExistMsg;
import com.zer0s2m.creeptenuous.models.user.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

import java.io.IOException;

public interface ControllerApiCreateUserDoc {

    /**
     * Creating a user in the system
     * @param user Data for creating a user in the system
     * @throws UserAlreadyExistException user already exists
     * @throws IOException if an I/O error occurs or the parent directory does not exist
     */
    @Operation(
            method = "POST",
            summary = "Creating a user",
            description = "Creating a user in the system",
            tags = { "User" },
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Data for creating a user in the system",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(example = "{" +
                                    "  \"role\": \"ROLE_USER\"," +
                                    "  \"login\": \"login\"," +
                                    "  \"email\": \"email@test.com\"," +
                                    "  \"password\": \"password\"," +
                                    "  \"name\": \"name\"," +
                                    "  \"dateOfBrith\": \"2023-06-01T16:13:04.957Z\"" +
                                    "}")
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "Successful creating user",
                            content = @Content
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "User with this [email;login] already exists.",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(
                                            implementation = UserAlreadyExistMsg.class
                                    ),
                                    examples = {
                                            @ExampleObject(
                                                    name = "Email already exists",
                                                    description = "User with this email already exists.",
                                                    value = "{" +
                                                            "  \"message\": \"User with this email already exists.\"," +
                                                            "  \"statusCode\": 400" +
                                                            "}"
                                            ),
                                            @ExampleObject(
                                                    name = "Login already exists",
                                                    description = "User with this login already exists.",
                                                    value = "{" +
                                                            "  \"message\": \"User with this login already exists.\"," +
                                                            "  \"statusCode\": 400" +
                                                            "}"
                                            )
                                    }
                            )
                    ),
            }
    )
    void create(final User user) throws UserAlreadyExistException, IOException;
}
