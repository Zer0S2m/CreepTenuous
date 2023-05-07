package com.zer0s2m.CreepTenuous.services.directory.copy.services.impl;

import com.zer0s2m.CreepTenuous.components.RootPath;
import com.zer0s2m.CreepTenuous.providers.build.os.core.Distribution;
import com.zer0s2m.CreepTenuous.services.core.Directory;
import com.zer0s2m.CreepTenuous.services.core.ServiceFileSystem;
import com.zer0s2m.CreepTenuous.services.directory.copy.enums.MethodCopyDirectory;
import com.zer0s2m.CreepTenuous.services.directory.copy.services.IServiceCopyDirectory;
import com.zer0s2m.CreepTenuous.providers.build.os.services.impl.ServiceBuildDirectoryPath;
import com.zer0s2m.CreepTenuous.providers.build.os.services.CheckIsExistsDirectoryService;
import com.zer0s2m.CreepTenuous.utils.UtilsFiles;
import com.zer0s2m.CreepTenuous.utils.WalkDirectoryInfo;
import com.zer0s2m.CreepTenuous.utils.containers.ContainerInfoFileSystemObject;

import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Stream;

@ServiceFileSystem("copy-directory")
public class ServiceCopyDirectory implements IServiceCopyDirectory, CheckIsExistsDirectoryService {
    private final ServiceBuildDirectoryPath buildDirectoryPath;

    private final String rootPath;

    private Path target;

    /**
     * <p>Parts of source paths on target</p>
     * <b>Key</b> - old source system path<br>
     * <b>Value</b> - new target system path
     */
    private final HashMap<String, String> paths = new HashMap<>();

    @Autowired
    public ServiceCopyDirectory(ServiceBuildDirectoryPath buildDirectoryPath, RootPath rootPath) {
        this.buildDirectoryPath = buildDirectoryPath;
        this.rootPath = rootPath.getRootPath();
    }

    /**
     * Copy directory
     * @param systemParents system path part directories
     * @param systemToParents system path part directories
     * @param systemNameDirectory system name directory
     * @param method method copy {@link com.zer0s2m.CreepTenuous.services.directory.copy.enums.MethodCopyDirectory}
     * @return information about copied file system objects
     * @throws IOException error system
     */
    @Override
    public List<ContainerInfoFileSystemObject> copy(
            List<String> systemParents,
            List<String> systemToParents,
            String systemNameDirectory,
            Integer method
    ) throws IOException {
        Path source = Paths.get(buildDirectoryPath.build(systemParents), systemNameDirectory);
        checkDirectory(source);

        if (Objects.equals(method, MethodCopyDirectory.FOLDER.getMethod())) {
            Path futurePath = Paths.get(buildDirectoryPath.build(systemToParents), Distribution.getUUID());
            if (!Files.exists(futurePath)) {
                this.target = Files.createDirectory(futurePath);
            }
        } else {
            this.target = Paths.get(buildDirectoryPath.build(systemToParents));
        }

        List<ContainerInfoFileSystemObject> attached = WalkDirectoryInfo.walkDirectory(source, this.target);

        try (Stream<Path> stream = Files.walk(source)) {
            stream.forEach(targetWalk -> {
                try {
                    buildingPaths(targetWalk);
                    Path newTarget = getTarget(targetWalk);
                    Files.copy(
                            targetWalk,
                            newTarget,
                            StandardCopyOption.REPLACE_EXISTING
                    );
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        }

        return attached;
    }

    /**
     * Building paths {@link ServiceCopyDirectory#paths}
     * @param source source system file object
     */
    private void buildingPaths(Path source) {
        String sourceStr = source.toString().replace(rootPath + Directory.SEPARATOR.get(), "");
        List<String> splitSource = Arrays.asList(sourceStr.split(Directory.SEPARATOR.get()));

        splitSource.forEach((part) -> {
            if (!this.paths.containsKey(part)) {
                if (Files.isRegularFile(source)) {
                    this.paths.put(part, UtilsFiles.getNewFileName(part));
                } else {
                    this.paths.put(part, Distribution.getUUID());
                }
            }
        });
    }

    /**
     * Get parts system names for target
     * @param source source system file object
     * @return parts system names for building path
     */
    private Path getTarget(Path source) {
        List<String> parts = new ArrayList<>();
        String sourceStr = source.toString().replace(rootPath + Directory.SEPARATOR.get(), "");
        List<String> splitSource = Arrays.asList(sourceStr.split(Directory.SEPARATOR.get()));

        splitSource.forEach((part) -> parts.add(this.paths.get(part)));

        return Paths.get(String.valueOf(this.target), String.join(Directory.SEPARATOR.get(), parts));
    }
}
