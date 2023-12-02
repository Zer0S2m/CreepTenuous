package com.zer0s2m.creeptenuous.module.search;

import java.util.List;
import java.util.UUID;

public record ContainerInfoSearchFileObject(

        List<UUID> systemParents,

        UUID systemName,

        String realName,

        String owner,

        boolean isFile,

        boolean isDirectory

) {
}
