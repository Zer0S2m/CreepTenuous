package com.zer0s2m.creeptenuous.common.http;

import io.swagger.v3.oas.annotations.media.Schema;

public record ResponseUploadAvatarUserApi(

        @Schema(
                title = "Avatar",
                description = "Uploaded avatar for the user",
                example = "avatars/avatar-1.png"
        )
        String avatar

) { }
