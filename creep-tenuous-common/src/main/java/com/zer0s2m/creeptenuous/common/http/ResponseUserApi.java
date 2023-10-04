package com.zer0s2m.creeptenuous.common.http;

import com.zer0s2m.creeptenuous.common.enums.UserRole;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Set;

public record ResponseUserApi(

        @Schema(description = "User login")
        String login,

        @Schema(description = "User email")
        String email,

        @Schema(description = "User name")
        String name,

        @Schema(description = "User roles")
        Set<UserRole> role,

        @Schema(description = "Deleting files if the user is deleted")
        Boolean isDeletingFilesWhenDeletingUser,

        @Schema(description = "transferring files to another user if the owner is deleted")
        String passingFilesToUser,

        @Schema(description = "Is the user blocked")
        Boolean isBlocked,

        @Schema(description = "Is the user temporarily blocked")
        Boolean isTemporarilyBlocked,

        @Schema(description = "Uploaded avatar for the user")
        String avatar

) { }
