package com.zer0s2m.creeptenuous.common.http;

import com.zer0s2m.creeptenuous.common.containers.ContainerInfoFileSystemObject;

import java.util.List;

public record ResponseCopyDirectoryApi(
        List<ContainerInfoFileSystemObject> objects
) { }
