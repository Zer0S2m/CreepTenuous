package com.zer0s2m.CreepTenuous.services.directory.manager.services;

import com.zer0s2m.CreepTenuous.providers.redis.models.DirectoryRedis;
import com.zer0s2m.CreepTenuous.services.directory.manager.containers.ContainerDataFiles;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public interface IBuilderDataFile {
    ContainerDataFiles build(ArrayList<List<Path>> paths);
    List<Object> build(List<DirectoryRedis> redisList);
}
