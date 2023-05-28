package com.zer0s2m.creeptenuous.services.redis.system;

import com.zer0s2m.creeptenuous.redis.models.DirectoryRedis;
import com.zer0s2m.creeptenuous.redis.models.FileRedis;
import com.zer0s2m.creeptenuous.redis.repositories.DirectoryRedisRepository;
import com.zer0s2m.creeptenuous.redis.repositories.FileRedisRepository;
import com.zer0s2m.creeptenuous.redis.services.system.ServiceManagerDirectoryRedis;
import com.zer0s2m.creeptenuous.security.jwt.providers.JwtProvider;
import com.zer0s2m.creeptenuous.services.redis.system.base.BaseServiceFileSystemRedisImpl;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service("service-manager-directory-redis")
public class ServiceManagerDirectoryRedisImpl extends BaseServiceFileSystemRedisImpl
        implements ServiceManagerDirectoryRedis {
    @Autowired
    public ServiceManagerDirectoryRedisImpl(
            DirectoryRedisRepository directoryRedisRepository,
            FileRedisRepository fileRedisRepository,
            JwtProvider jwtProvider
    ) {
        super(directoryRedisRepository, fileRedisRepository, jwtProvider);
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

        directoryRedis.forEach(objRedis -> buildJSON(
                data,
                objRedis.getSystemNameDirectory(),
                objRedis.getIsFile(),
                objRedis.getIsDirectory(),
                objRedis.getRealNameDirectory()
        ));
        fileRedis.forEach(objRedis -> buildJSON(
                data,
                objRedis.getSystemNameFile(),
                objRedis.getIsFile(),
                objRedis.getIsDirectory(),
                objRedis.getRealNameFile()
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
     */
    private void buildJSON(
            JSONArray data,
            String systemName,
            Boolean isFile,
            Boolean isDirectory,
            String realName
    ) {
        JSONObject obj = new JSONObject();

        obj.put("systemName", systemName);
        obj.put("realName", realName);
        obj.put("isFile", isFile);
        obj.put("isDirectory", isDirectory);

        data.put(obj);
    }
}
