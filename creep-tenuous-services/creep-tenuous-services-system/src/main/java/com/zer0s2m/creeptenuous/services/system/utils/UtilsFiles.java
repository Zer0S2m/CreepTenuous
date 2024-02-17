package com.zer0s2m.creeptenuous.services.system.utils;

import com.zer0s2m.creeptenuous.common.enums.Directory;
import com.zer0s2m.creeptenuous.core.atomic.Distribution;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface UtilsFiles {

    /**
     * Get extension file
     * @param nameFile name file
     * @return extension
     */
    @Contract(pure = true)
    static @Nullable String getExtensionFile(@NotNull String nameFile) {
        String[] partFileName = nameFile.split("\\.");
        if (partFileName.length == 1) {
            return null;
        }
        return partFileName[partFileName.length - 1];
    }

    /**
     * Get new file name (for system)
     * @param nameFile real name file
     * @return system name file
     */
    static String getNewFileName(String nameFile) {
        String extensionFile = getExtensionFile(nameFile);
        String newFileName = Distribution.getUUID();
        if (extensionFile != null) {
            return newFileName + "." + extensionFile;
        }
        return newFileName;
    }

    /**
     * Get file name
     * @param rawNameFile Raw name file (path)
     * @return file name
     */
    static String getNameFileRawStr(@NotNull String rawNameFile) {
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
