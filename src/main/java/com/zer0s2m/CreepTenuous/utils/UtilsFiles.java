package com.zer0s2m.CreepTenuous.utils;

import com.zer0s2m.CreepTenuous.providers.build.os.core.Distribution;
import com.zer0s2m.CreepTenuous.services.core.Directory;

public interface UtilsFiles {
    static String getExtensionFile(String nameFile) {
        String[] partFileName = nameFile.split("\\.");
        if (partFileName.length == 1) {
            return null;
        }
        return partFileName[partFileName.length - 1];
    }

    static String getNewFileName(String nameFile) {
        String extensionFile = getExtensionFile(nameFile);
        String newFileName = Distribution.getUUID();
        if (extensionFile != null) {
            return newFileName + "." + extensionFile;
        }
        return newFileName;
    }

    static String getNameFileRawStr(String rawNameFile) {
        String newRawNameFile;
        if (rawNameFile.substring(rawNameFile.length() - 1).equals(Directory.SEPARATOR.get())) {
            newRawNameFile = rawNameFile.substring(0, rawNameFile.length() - 1);
        } else {
            newRawNameFile = rawNameFile;
        }
        String[] parts = newRawNameFile.split(Directory.SEPARATOR.get());
        return parts[parts.length - 1];
    }
}
