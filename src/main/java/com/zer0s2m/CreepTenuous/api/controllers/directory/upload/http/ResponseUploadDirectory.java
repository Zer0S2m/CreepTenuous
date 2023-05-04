package com.zer0s2m.CreepTenuous.api.controllers.directory.upload.http;

import com.zer0s2m.CreepTenuous.services.directory.upload.containers.ContainerDataUploadFile;

import java.util.List;

public record ResponseUploadDirectory(Boolean success, List<ContainerDataUploadFile> data) { }
