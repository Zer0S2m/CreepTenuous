package com.zer0s2m.creeptenuous.common.http;

import java.util.List;

public record ResponseManagerDirectoryApi(
        List<String> systemParents,
        Integer levelDirectory,
        List<Object> objects
) { }
