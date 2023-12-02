package com.zer0s2m.creeptenuous.module.search;

import java.util.List;
import java.util.UUID;

public record DataSearchFileObject(

        List<UUID> systemParents,

        String partRealName,

        SearchFileObject type

) {
}
