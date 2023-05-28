package com.zer0s2m.creeptenuous.common.http;

import com.zer0s2m.creeptenuous.common.containers.ContainerDataUploadFileSystemObject;

import java.util.List;

public record ResponseUploadDirectoryApi(
        Boolean success,
        List<ContainerDataUploadFileSystemObject> data
) { }
