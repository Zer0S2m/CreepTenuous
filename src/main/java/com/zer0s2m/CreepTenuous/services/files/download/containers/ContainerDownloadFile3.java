package com.zer0s2m.CreepTenuous.services.files.download.containers;

public record ContainerDownloadFile3<T, E>(
        T byteContent,
        E mimeType,
        E filename
) {
}
