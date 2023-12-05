package com.zer0s2m.creeptenuous.module.search.services;

import com.zer0s2m.creeptenuous.common.components.RootPath;
import com.zer0s2m.creeptenuous.common.enums.Directory;
import com.zer0s2m.creeptenuous.module.search.ContainerInfoSearchFileObject;
import com.zer0s2m.creeptenuous.module.search.SearchFileObject;
import com.zer0s2m.creeptenuous.module.search.ServiceSearchFileObject;
import com.zer0s2m.creeptenuous.redis.models.DirectoryRedis;
import com.zer0s2m.creeptenuous.redis.models.FileRedis;
import com.zer0s2m.creeptenuous.redis.models.RightUserFileSystemObjectRedis;
import com.zer0s2m.creeptenuous.redis.models.base.IBaseRedis;
import com.zer0s2m.creeptenuous.redis.repository.DirectoryRedisRepository;
import com.zer0s2m.creeptenuous.redis.repository.FileRedisRepository;
import com.zer0s2m.creeptenuous.redis.services.resources.ServiceRedisManagerResources;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service("service-search-file-object")
public class ServiceSearchFileObjectImpl implements ServiceSearchFileObject {

    private Collection<String> systemParents;

    private String userLogin;

    private String partRealName;

    private SearchFileObject searchFileObject;

    private final DirectoryRedisRepository directoryRedisRepository;

    private final FileRedisRepository fileRedisRepository;

    private final ServiceRedisManagerResources serviceRedisManagerResources;

    private final String rootPath = new RootPath().getRootPath();

    @Autowired
    public ServiceSearchFileObjectImpl(
            DirectoryRedisRepository directoryRedisRepository,
            FileRedisRepository fileRedisRepository,
            ServiceRedisManagerResources serviceRedisManagerResources) {
        this.directoryRedisRepository = directoryRedisRepository;
        this.fileRedisRepository = fileRedisRepository;
        this.serviceRedisManagerResources = serviceRedisManagerResources;
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
    @SuppressWarnings("unchecked")
    public List<ContainerInfoSearchFileObject> search() {
        List<DirectoryRedis> directoryRedisList = new ArrayList<>();
        List<FileRedis> fileRedisList = new ArrayList<>();
        List<String> idsFileObjectFromRightUser = getFileSystemObjectSystemNameFrmoRightUser(
                serviceRedisManagerResources.getRightUserFileSystemObjectByLogin(getUserLogin()));

        if (getTypeSearchFileObject() == SearchFileObject.FILE) {
            fileRedisList.addAll(fileRedisRepository.getFileRedisByLogin(getUserLogin()));
            fileRedisList.addAll(serviceRedisManagerResources
                    .getResourceFileRedis(idsFileObjectFromRightUser));
        } else if (getTypeSearchFileObject() == SearchFileObject.DIRECTORY) {
            directoryRedisList.addAll(directoryRedisRepository.getDirectoryRedisByLogin(getUserLogin()));
            directoryRedisList.addAll(serviceRedisManagerResources
                    .getResourceDirectoryRedis(idsFileObjectFromRightUser));
        } else {
            fileRedisList.addAll(fileRedisRepository.getFileRedisByLogin(getUserLogin()));
            fileRedisList.addAll(serviceRedisManagerResources
                    .getResourceFileRedis(idsFileObjectFromRightUser));
            directoryRedisList.addAll(directoryRedisRepository.getDirectoryRedisByLogin(getUserLogin()));
            directoryRedisList.addAll(serviceRedisManagerResources
                    .getResourceDirectoryRedis(idsFileObjectFromRightUser));
        }

        directoryRedisList = (List<DirectoryRedis>) filterFileObjectsByNestingLevel(directoryRedisList);
        fileRedisList = (List<FileRedis>) filterFileObjectsByNestingLevel(fileRedisList);
        directoryRedisList = (List<DirectoryRedis>) filterFileObjectByPartRealName(directoryRedisList);
        fileRedisList = (List<FileRedis>) filterFileObjectByPartRealName(fileRedisList);

        final List<ContainerInfoSearchFileObject> result = new ArrayList<>();
        result.addAll(collectSearchResult(directoryRedisList));
        result.addAll(collectSearchResult(fileRedisList));

        return result;
    }

    /**
     * Filter file objects by directory nesting level.
     * @param fileObjects File objects.
     * @return Filtered file objects.
     */
    private List<? extends IBaseRedis> filterFileObjectsByNestingLevel(
            final @NotNull List<? extends IBaseRedis> fileObjects) {
        if (getSystemParents().isEmpty()) {
            return fileObjects;
        }

        return fileObjects
                .stream()
                .filter(fileObject -> {
                    List<String> pathSplit = Arrays.asList(
                            fileObject
                                    .getPath()
                                    .replace(rootPath + Directory.SEPARATOR.get(), "")
                                    .split(Directory.SEPARATOR.get()));
                    return new HashSet<>(pathSplit).containsAll(getSystemParents());
                })
                .toList();
    }

    /**
     * Filtering file objects by part of the real name.
     * @param fileObjects File objects.
     * @return Filtered file objects.
     */
    private List<? extends IBaseRedis> filterFileObjectByPartRealName(
            final @NotNull List<? extends IBaseRedis> fileObjects) {
        return fileObjects
                .stream()
                .filter(fileObject -> fileObject.getRealName().contains(getPartRealName()))
                .toList();
    }

    /**
     * Collect the final search result.
     * @param fileObjects File objects.
     * @return Search results.
     */
    private @NotNull List<ContainerInfoSearchFileObject> collectSearchResult(
            final @NotNull List<? extends IBaseRedis> fileObjects) {
        final List<ContainerInfoSearchFileObject> collectedResult = new ArrayList<>();
        fileObjects.forEach(fileObject -> collectedResult.add(new ContainerInfoSearchFileObject(
                    getSystemParents(),
                    UUID.fromString(fileObject.getSystemName()),
                    fileObject.getRealName(),
                    fileObject.getLogin(),
                    fileObject.getIsFile(),
                    fileObject.getIsDirectory())));
        return collectedResult;
    }

    /**
     * Unpack the unique permission key and get the system name of the file object.
     * @param rightUserFileSystemObjectRedis Rights to interact with file objects.
     * @return System names of file objects.
     */
    private @NotNull List<String> getFileSystemObjectSystemNameFrmoRightUser(
            final @NotNull List<RightUserFileSystemObjectRedis> rightUserFileSystemObjectRedis) {
        final List<String> systemNames = new ArrayList<>();
        rightUserFileSystemObjectRedis.forEach(obj -> systemNames.add(unpackingUniqueKey(obj.getFileSystemObject())));
        return systemNames;
    }

}
