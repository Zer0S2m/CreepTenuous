package com.zer0s2m.creeptenuous.common.containers;

public record ContainerDataDownloadFile<T, E>(
        T byteContent,
        E mimeType,
        E filename
) { }
