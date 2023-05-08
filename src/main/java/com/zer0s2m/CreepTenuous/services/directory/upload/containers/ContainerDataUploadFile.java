package com.zer0s2m.CreepTenuous.services.directory.upload.containers;

import java.nio.file.Path;

/**
 * Archive unpacking data. Includes information about <b>files</b> and <b>directories</b>
 * @param realName
 * @param systemName
 * @param systemPath
 * @param isFile
 * @param isDirectory
 */
public record ContainerDataUploadFile(
        String realName,
        String systemName,
        Path systemPath,
        Boolean isFile,
        Boolean isDirectory
) {
}
