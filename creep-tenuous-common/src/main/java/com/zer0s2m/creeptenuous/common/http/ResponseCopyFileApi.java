package com.zer0s2m.creeptenuous.common.http;

import com.zer0s2m.creeptenuous.common.containers.ContainerDataCopyFile;

import java.util.List;

public record ResponseCopyFileApi(
        List<ContainerDataCopyFile> files
) { }
