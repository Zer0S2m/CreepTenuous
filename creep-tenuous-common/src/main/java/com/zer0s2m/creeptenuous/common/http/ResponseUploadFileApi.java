package com.zer0s2m.creeptenuous.common.http;

import java.util.List;

public record ResponseUploadFileApi(
        List<ResponseObjectUploadFileApi> objects
) { }
