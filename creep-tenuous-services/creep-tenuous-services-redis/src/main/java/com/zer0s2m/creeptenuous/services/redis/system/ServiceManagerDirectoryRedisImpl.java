package com.zer0s2m.creeptenuous.services.redis.system;

import com.zer0s2m.creeptenuous.redis.models.DirectoryRedis;
import com.zer0s2m.creeptenuous.redis.models.FileRedis;
import com.zer0s2m.creeptenuous.redis.repository.DirectoryRedisRepository;
import com.zer0s2m.creeptenuous.redis.repository.FileRedisRepository;
import com.zer0s2m.creeptenuous.redis.repository.FrozenFileSystemObjectRedisRepository;
import com.zer0s2m.creeptenuous.redis.services.system.ServiceManagerDirectoryRedis;
import com.zer0s2m.creeptenuous.repository.user.UserColorDirectoryRepository;
import com.zer0s2m.creeptenuous.security.jwt.providers.JwtProvider;
import com.zer0s2m.creeptenuous.services.redis.system.base.BaseServiceFileSystemRedisManagerRightsAccessImpl;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Service for viewing file system objects in Redis
 */
@Service("service-manager-directory-redis")
public class ServiceManagerDirectoryRedisImpl extends BaseServiceFileSystemRedisManagerRightsAccessImpl
        implements ServiceManagerDirectoryRedis {

    private final UserColorDirectoryRepository userColorDirectoryRepository;

    @Autowired
    public ServiceManagerDirectoryRedisImpl(
            DirectoryRedisRepository directoryRedisRepository,
            FileRedisRepository fileRedisRepository,
            FrozenFileSystemObjectRedisRepository frozenFileSystemObjectRedisRepository,
            UserColorDirectoryRepository userColorDirectoryRepository,
            JwtProvider jwtProvider) {
        super(directoryRedisRepository, fileRedisRepository, frozenFileSystemObjectRedisRepository,
                jwtProvider);

        this.userColorDirectoryRepository = userColorDirectoryRepository;
    }

    /**
     * Get data file system object
     * @param systemNamesFileSystemObject system path object ids {@link DirectoryRedis#getRealNameDirectory()}
     *                                    or {@link FileRedis#getRealNameFile()}
     * @return json array
     */
    @Override
    public List<Object> build(List<String> systemNamesFileSystemObject) {
        JSONArray data = new JSONArray();

        Iterable<DirectoryRedis> directoryRedis = directoryRedisRepository.findAllById(systemNamesFileSystemObject);
        Iterable<FileRedis> fileRedis = fileRedisRepository.findAllById(systemNamesFileSystemObject);
        Map<String, String> mapColors = getUserColorsDirectory(systemNamesFileSystemObject);

        directoryRedis.forEach(objRedis -> buildJSON(
                data,
                objRedis.getSystemNameDirectory(),
                objRedis.getIsFile(),
                objRedis.getIsDirectory(),
                objRedis.getRealNameDirectory(),
                mapColors.getOrDefault(objRedis.getSystemNameDirectory(), null)
        ));
        fileRedis.forEach(objRedis -> buildJSON(
                data,
                objRedis.getSystemNameFile(),
                objRedis.getIsFile(),
                objRedis.getIsDirectory(),
                objRedis.getRealNameFile(),
                null
        ));

        if (data.length() > 0) {
            return data.toList();
        } else {
            return new ArrayList<>();
        }
    }

    /**
     * Build json object
     * @param data array json
     * @param systemName system name file system object
     * @param isFile is file
     * @param isDirectory is directory
     * @param realName real name file system object
     * @param color color directory
     */
    private void buildJSON(
            @NotNull JSONArray data,
            String systemName,
            Boolean isFile,
            Boolean isDirectory,
            String realName,
            String color
    ) {
        JSONObject obj = new JSONObject();

        obj.put("systemName", systemName);
        obj.put("realName", realName);
        obj.put("isFile", isFile);
        obj.put("isDirectory", isDirectory);
        obj.put("color", color);

        data.put(obj);
    }

    /**
     * Pulls color schemes for directories and returns a map of set colors for a user directory
     * @param ids names directories. Must not be {@literal null}.
     * @return map of installed colors for user directory
     */
    private @NotNull Map<String, String> getUserColorsDirectory(@NotNull List<String> ids) {
        Map<String, String> mapColors = new HashMap<>();

        userColorDirectoryRepository.findAllByDirectoryIn(ids
                        .stream()
                        .map(UUID::fromString)
                        .toList())
                .forEach(userColorDirectory -> mapColors.put(
                        userColorDirectory.getDirectory().toString(), userColorDirectory.getColor()
                ));

        return mapColors;
    }

}
