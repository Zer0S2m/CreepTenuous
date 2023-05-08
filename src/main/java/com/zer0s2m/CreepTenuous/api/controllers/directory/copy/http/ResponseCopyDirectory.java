package com.zer0s2m.CreepTenuous.api.controllers.directory.copy.http;

import com.zer0s2m.CreepTenuous.utils.containers.ContainerInfoFileSystemObject;

import java.util.List;

public record ResponseCopyDirectory(List<ContainerInfoFileSystemObject> objects) {
}
