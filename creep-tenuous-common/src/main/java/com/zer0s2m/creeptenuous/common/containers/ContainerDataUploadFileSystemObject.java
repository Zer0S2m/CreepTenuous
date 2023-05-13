package com.zer0s2m.creeptenuous.common.containers;

import java.nio.file.Path;

/**
 * Archive unpacking data. Includes information about <b>files</b> and <b>directories</b>
 * @param realName
 * @param systemName
 * @param systemPath
 * @param isFile
 * @param isDirectory
 */
public record ContainerDataUploadFileSystemObject(
        String realName,
        String systemName,
        Path systemPath,
        Boolean isFile,
        Boolean isDirectory
) {
}
