package com.zer0s2m.CreepTenuous.api.controllers.files.upload.http;

import java.nio.file.Path;

public record ResponseUploadFile(String fileName, Boolean success, Path path) {
}
