package com.zer0s2m.creeptenuous.common.containers;

import java.nio.file.Path;

public record ContainerDataCreateFile(

        String realNameFile,

        String systemNameFile,

        Path realPathFile,

        Path systemPathFile

) { }
