package com.zer0s2m.creeptenuous.services.redis.system;

import com.zer0s2m.creeptenuous.common.containers.ContainerUserColorDirectoryInfo;
import com.zer0s2m.creeptenuous.common.query.IMapFileObjectToCategory;
import com.zer0s2m.creeptenuous.models.user.UserColor;
import com.zer0s2m.creeptenuous.redis.models.DirectoryRedis;
import com.zer0s2m.creeptenuous.redis.models.FileRedis;
import com.zer0s2m.creeptenuous.redis.repository.DirectoryRedisRepository;
import com.zer0s2m.creeptenuous.redis.repository.FileRedisRepository;
import com.zer0s2m.creeptenuous.redis.repository.FrozenFileSystemObjectRedisRepository;
import com.zer0s2m.creeptenuous.redis.services.system.ServiceManagerDirectoryRedis;
import com.zer0s2m.creeptenuous.repository.user.CategoryFileSystemObjectRepository;
import com.zer0s2m.creeptenuous.repository.user.UserColorDirectoryRepository;
import com.zer0s2m.creeptenuous.security.jwt.JwtProvider;
import com.zer0s2m.creeptenuous.services.redis.system.base.BaseServiceFileSystemRedisManagerRightsAccessImpl;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

/**
 * Service for viewing file system objects in Redis
 */
@Service("service-manager-directory-redis")
public class ServiceManagerDirectoryRedisImpl extends BaseServiceFileSystemRedisManagerRightsAccessImpl
        implements ServiceManagerDirectoryRedis {

    private final UserColorDirectoryRepository userColorDirectoryRepository;

    private final CategoryFileSystemObjectRepository categoryFileSystemObjectRepository;

    @Autowired
    public ServiceManagerDirectoryRedisImpl(
            DirectoryRedisRepository directoryRedisRepository,
            FileRedisRepository fileRedisRepository,
            FrozenFileSystemObjectRedisRepository frozenFileSystemObjectRedisRepository,
            UserColorDirectoryRepository userColorDirectoryRepository,
            CategoryFileSystemObjectRepository categoryFileSystemObjectRepository,
            JwtProvider jwtProvider) {
        super(directoryRedisRepository, fileRedisRepository, frozenFileSystemObjectRedisRepository,
                jwtProvider);

        this.userColorDirectoryRepository = userColorDirectoryRepository;
        this.categoryFileSystemObjectRepository = categoryFileSystemObjectRepository;
    }

    /**
     * Get data file system object
     * @param systemNamesFileSystemObject system path object ids {@link DirectoryRedis#getRealName()}
     *                                    or {@link FileRedis#getRealName()}
     * @return json array
     */
    @Override
    public List<Object> build(List<String> systemNamesFileSystemObject) {
        JSONArray data = new JSONArray();

        Iterable<DirectoryRedis> directoryRedis = directoryRedisRepository.findAllById(systemNamesFileSystemObject);
        Iterable<FileRedis> fileRedis = fileRedisRepository.findAllById(systemNamesFileSystemObject);
        Map<String, ContainerUserColorDirectoryInfo> mapColors = getUserColorsDirectory(systemNamesFileSystemObject);

        List<IMapFileObjectToCategory> fileObjectSettingToCategory = categoryFileSystemObjectRepository
                .getMapFileObjectSettingToCategory(getLoginUser());
        HashMap<String, Long> mapFileObjectToCategories = new HashMap<>();
        fileObjectSettingToCategory.forEach((entity) ->
                entity.getFileSystemObjects().forEach((fileObject)
                        -> mapFileObjectToCategories.put(fileObject.toString(), entity.getUserCategoryId())));

        directoryRedis.forEach(objRedis -> {
            ContainerUserColorDirectoryInfo colorDirectoryInfo = mapColors.getOrDefault(
                    objRedis.getSystemName(), null);

            buildJSON(
                    data,
                    objRedis.getSystemName(),
                    objRedis.getIsFile(),
                    objRedis.getIsDirectory(),
                    objRedis.getRealName(),
                    objRedis.getCreatedAt(),
                    colorDirectoryInfo != null ? colorDirectoryInfo.color() : null,
                    colorDirectoryInfo != null ? colorDirectoryInfo.colorId() : null,
                    mapFileObjectToCategories.get(objRedis.getSystemName()),
                    objRedis.getLogin()
            );
        });
        fileRedis.forEach(objRedis -> buildJSON(
                data,
                objRedis.getSystemName(),
                objRedis.getIsFile(),
                objRedis.getIsDirectory(),
                objRedis.getRealName(),
                objRedis.getCreatedAt(),
                null,
                null,
                mapFileObjectToCategories.get(objRedis.getSystemName()),
                objRedis.getLogin()
        ));

        if (!data.isEmpty()) {
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
     * @param createdAt File object creation date
     * @param color color directory
     * @param colorId color ID
     * @param categoryId custom category
     */
    private void buildJSON(
            @NotNull JSONArray data,
            String systemName,
            Boolean isFile,
            Boolean isDirectory,
            String realName,
            LocalDateTime createdAt,
            String color,
            Long colorId,
            Long categoryId,
            String owner
    ) {
        JSONObject obj = new JSONObject();

        obj.put("systemName", systemName);
        obj.put("realName", realName);
        obj.put("isFile", isFile);
        obj.put("isDirectory", isDirectory);
        obj.put("createdAt", createdAt != null ? createdAt.toString() : null);
        obj.put("color", color);
        obj.put("colorId", colorId);
        obj.put("categoryId", categoryId);
        obj.put("owner", owner);

        data.put(obj);
    }

    /**
     * Pulls color schemes for directories and returns a map of set colors for a user directory
     * @param ids names directories. Must not be {@literal null}.
     * @return map of installed colors for user directory
     */
    private @NotNull Map<String, ContainerUserColorDirectoryInfo> getUserColorsDirectory(@NotNull List<String> ids) {
        Map<String, ContainerUserColorDirectoryInfo> mapColors = new HashMap<>();

        userColorDirectoryRepository.findAllByDirectoryIn(ids
                        .stream()
                        .map(UUID::fromString)
                        .toList())
                .forEach(userColorDirectory -> {
                    UserColor userColor = userColorDirectory.getColor();
                    mapColors.put(
                            userColorDirectory.getDirectory().toString(),
                            new ContainerUserColorDirectoryInfo(
                                    userColor == null ? null : userColor.getColor(),
                                    userColor == null ? null : userColor.getId()
                            )
                    );
                });

        return mapColors;
    }

}
