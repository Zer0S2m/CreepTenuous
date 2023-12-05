package com.zer0s2m.creeptenuous.module.search;

import java.util.Collection;
import java.util.UUID;

public record ContainerInfoSearchFileObject(

        Collection<String> systemParents,

        UUID systemName,

        String realName,

        String owner,

        boolean isFile,

        boolean isDirectory

) {
}
