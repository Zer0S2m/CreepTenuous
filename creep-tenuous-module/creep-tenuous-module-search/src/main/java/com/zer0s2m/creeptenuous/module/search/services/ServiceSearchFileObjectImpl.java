package com.zer0s2m.creeptenuous.module.search.services;

import com.zer0s2m.creeptenuous.module.search.ContainerInfoSearchFileObject;
import com.zer0s2m.creeptenuous.module.search.SearchFileObject;
import com.zer0s2m.creeptenuous.module.search.ServiceSearchFileObject;
import com.zer0s2m.creeptenuous.redis.repository.DirectoryRedisRepository;
import com.zer0s2m.creeptenuous.redis.repository.FileRedisRepository;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service("service-search-file-object")
public class ServiceSearchFileObjectImpl implements ServiceSearchFileObject {

    private Collection<String> systemParents;

    private String userLogin;

    private String partRealName;

    private SearchFileObject searchFileObject;

    private final DirectoryRedisRepository directoryRedisRepository;

    private final FileRedisRepository fileRedisRepository;

    @Autowired
    public ServiceSearchFileObjectImpl(
            DirectoryRedisRepository directoryRedisRepository, FileRedisRepository fileRedisRepository) {
        this.directoryRedisRepository = directoryRedisRepository;
        this.fileRedisRepository = fileRedisRepository;
    }

    /**
     * Set system file object names to set the directory nesting level.
     *
     * @param systemParents System names of file objects.
     * @return Class object.
     */
    @Override
    public ServiceSearchFileObject setSystemParents(Collection<String> systemParents) {
        this.systemParents = systemParents;
        return this;
    }

    /**
     * Get system names of file objects.
     *
     * @return System names of file objects.
     */
    @Override
    public Collection<String> getSystemParents() {
        return systemParents;
    }

    /**
     * Set the user login for which the search will be performed.
     *
     * @param userLogin User login.
     * @return Class object.
     */
    @Override
    public ServiceSearchFileObject setUserLogin(@NotNull String userLogin) {
        this.userLogin = userLogin.trim();
        return this;
    }

    /**
     * Get user login.
     *
     * @return User login.
     */
    @Override
    public String getUserLogin() {
        return userLogin;
    }

    /**
     * Set part of the real name of the file object that will be searched for
     *
     * @param partRealName Part of the real name of the file object.
     * @return Class object.
     */
    @Override
    public ServiceSearchFileObject setPartRealName(@NotNull String partRealName) {
        this.partRealName = partRealName.trim();
        return this;
    }

    /**
     * Get part of the real name of the file object.
     *
     * @return Part of the real name of the file object.
     */
    @Override
    public String getPartRealName() {
        return partRealName;
    }

    /**
     * Set the file object type. Rule for searching file objects.
     *
     * @param searchFileObject File object type.
     * @return Class object.
     */
    @Override
    public ServiceSearchFileObject setTypeSearchFileObject(SearchFileObject searchFileObject) {
        this.searchFileObject = searchFileObject;
        return this;
    }

    /**
     * Get the file object type.
     *
     * @return File object type.
     */
    @Override
    public SearchFileObject getTypeSearchFileObject() {
        return searchFileObject;
    }

    @Override
    public List<ContainerInfoSearchFileObject> search() {
        return new ArrayList<>();
    }

}
