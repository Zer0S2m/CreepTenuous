package com.zer0s2m.creeptenuous.api.documentation.controllers;

import com.zer0s2m.creeptenuous.common.exceptions.AccountIsBlockedException;
import com.zer0s2m.creeptenuous.common.exceptions.UserNotFoundException;
import com.zer0s2m.creeptenuous.common.exceptions.UserNotValidPasswordException;
import com.zer0s2m.creeptenuous.security.jwt.NoValidJwtRefreshTokenException;
import com.zer0s2m.creeptenuous.security.jwt.JwtRefreshTokenRequest;
import com.zer0s2m.creeptenuous.security.jwt.JwtResponse;
import com.zer0s2m.creeptenuous.security.jwt.JwtUserRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

public interface ControllerApiAuthDoc {

    /**
     * User authorization
     * @param user data for login
     * @return JWT tokens
     * @throws UserNotFoundException user not found
     * @throws UserNotValidPasswordException invalid password
     * @throws AccountIsBlockedException the account is blocked
     */
    @Operation(
            method = "POST",
            summary = "User authorization",
            description = "User authorization",
            tags = { "Authorization" },
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Data for user authorization",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = JwtUserRequest.class)
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successful user authorization",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = JwtResponse.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(ref = "#/components/schemas/Unauthorized")
                            )
                    )
            }
    )
    JwtResponse login(final JwtUserRequest user) throws UserNotFoundException, UserNotValidPasswordException,
            AccountIsBlockedException;

    /**
     * Get JWT access token
     * @param request JWT refresh token
     * @return JWT tokens
     * @throws UserNotFoundException user not found
     * @throws NoValidJwtRefreshTokenException invalid JWT refresh token
     */
    @Operation(
            method = "POST",
            summary = "Getting a JWT access token",
            description = "Getting a JWT access token",
            tags = { "Authorization" },
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Data getting a JWT access token",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = JwtRefreshTokenRequest.class)
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successful getting JWT access token",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = JwtResponse.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(ref = "#/components/schemas/Unauthorized")
                            )
                    )
            }
    )
    JwtResponse access(final JwtRefreshTokenRequest request) throws UserNotFoundException,
            NoValidJwtRefreshTokenException;

    /**
     * Get JWT refresh token
     * @param request JWT refresh token
     * @return JWT tokens
     * @throws NoValidJwtRefreshTokenException invalid JWT refresh token
     * @throws UserNotFoundException user not found
     */
    @Operation(
            method = "POST",
            summary = "Getting a JWT refresh token",
            description = "Getting a JWT refresh token",
            tags = { "Authorization" },
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Data getting a JWT refresh token",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = JwtRefreshTokenRequest.class)
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successful getting JWT refresh token",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = JwtResponse.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(ref = "#/components/schemas/Unauthorized")
                            )
                    )
            }
    )
    JwtResponse refresh(final JwtRefreshTokenRequest request) throws NoValidJwtRefreshTokenException,
            UserNotFoundException;

    /**
     * Logout user
     * @param accessToken RAW ACCESS jwt token
     */
    @Operation(
            method = "GET",
            summary = "Logout user",
            description = "Logout user",
            tags = { "Authorization" },
            security = @SecurityRequirement(name = "Bearer Authentication"),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successful logout",
                            content = @Content
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(ref = "#/components/schemas/Unauthorized")
                            )
                    )
            }
    )
    void logout(@Parameter(hidden = true) String accessToken);

}
