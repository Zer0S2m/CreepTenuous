package com.zer0s2m.CreepTenuous.utils;

import com.zer0s2m.CreepTenuous.providers.build.os.core.Distribution;

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
}
