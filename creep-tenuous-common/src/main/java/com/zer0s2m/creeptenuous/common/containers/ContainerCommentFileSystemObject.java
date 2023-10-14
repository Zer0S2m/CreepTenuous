package com.zer0s2m.creeptenuous.common.containers;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.UUID;

public record ContainerCommentFileSystemObject(

        @Schema(description = "ID", example = "1", format = "int64")
        Long id,

        @Schema(description = "Comment", example = "Comment")
        String comment,

        @Schema(
                description = "The file object to which the comment belongs",
                example = "80a61e71-bafe-4663-8a5d-605b0d02af7b"
        )
        UUID fileSystemObject,

        @Schema(description = "Date the comment was created", example = "2023-10-14T14:34:06.545122")
        LocalDateTime createdAt,

        @Schema(description = "Unique ID of the parent comment", example = "1", format = "int64")
        Long parentId,

        @ArraySchema(arraySchema = @Schema(
                description = "Child comments",
                example = "[{" +
                        "\"id\": 3," +
                        "\"comment\": \"Comment\"," +
                        "\"fileSystemObject\": \"73a21b4b-b513-46ee-ad9c-623105829ba9\"," +
                        "\"createdAt\": \"2023-10-14T12:30:20.583808\"," +
                        "\"parentId\": 1," +
                        "\"childs\": []" +
                        "}]"
        ))
        Collection<ContainerCommentFileSystemObject> childs

) {
}
