package com.zer0s2m.creeptenuous.common.utils;

import com.zer0s2m.creeptenuous.common.enums.Directory;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

public interface UtilsFileSystem {

    /**
     * Clear extension of file object in path.
     * @param path Target.
     * @return Cleared path.
     */
    @Contract(pure = true)
    static @NotNull String clearSystemPathFile(@NotNull Path path) {
        List<String> pathSplit = Arrays.asList(path.toString().split(Directory.SEPARATOR.get()));
        pathSplit.set(pathSplit.size() - 1, pathSplit.getLast().split("\\.")[0]);
        return String.join("/", pathSplit);
    }

}
