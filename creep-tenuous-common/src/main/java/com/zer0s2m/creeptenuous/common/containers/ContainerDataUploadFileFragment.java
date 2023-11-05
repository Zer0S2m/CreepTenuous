package com.zer0s2m.creeptenuous.common.containers;

import java.util.Collection;
import java.nio.file.Path;

public record ContainerDataUploadFileFragment(

        String originalName,

        String systemName,

        Collection<Path> partsFragments,

        Path systemPath

) {
}
