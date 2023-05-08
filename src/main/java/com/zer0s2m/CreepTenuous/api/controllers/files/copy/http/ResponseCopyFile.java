package com.zer0s2m.CreepTenuous.api.controllers.files.copy.http;

import com.zer0s2m.CreepTenuous.services.files.move.containers.ContainerMovingFiles;

import java.util.List;

public record ResponseCopyFile(List<ContainerMovingFiles> files) { }
