package com.zer0s2m.creeptenuous.common.utils;

import com.zer0s2m.creeptenuous.common.data.DataDownloadDirectorySelectPartApi;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Utilities for working with incoming data in the API
 */
public interface UtilsDataApi {

    /**
     * Collect unique parts of the system path from information about file system objects
     * @param attached file system object data
     * @return unique parts of the system path
     */
    @Contract(pure = true)
    static @NotNull List<String> collectUniqueSystemParentsForDownloadDirectorySelect(
            @NotNull List<DataDownloadDirectorySelectPartApi> attached) {
        Set<String> uniqueSystemParents = new HashSet<>();
        attached.forEach(attach -> uniqueSystemParents.addAll(attach.systemParents()));
        return uniqueSystemParents
                .stream()
                .toList();
    }

    /**
     * Collect unique system names from information about file system objects
     * @param attached file system object data
     * @return unique system names
     */
    static List<String> collectUniqueSystemNameForDownloadDirectorySelect(
            @NotNull List<DataDownloadDirectorySelectPartApi> attached) {
        Set<String> uniqueSystemName = new HashSet<>();
        attached.forEach(attach -> uniqueSystemName.add(attach.systemName()));
        return uniqueSystemName
                .stream()
                .toList();
    }

}
