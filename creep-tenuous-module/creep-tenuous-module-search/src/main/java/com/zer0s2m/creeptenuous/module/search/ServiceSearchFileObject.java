package com.zer0s2m.creeptenuous.module.search;

import com.zer0s2m.creeptenuous.redis.services.security.CollectUniqueKeyRightUserFileObject;

import java.util.Collection;
import java.util.List;

/**
 * Interface for implementing the main component of the file object search module.
 * <p>
 * Search criteria:
 * <ul>
 *     <li>Corresponds to the directory nesting level.</li>
 *     <li>Owner of the file object.</li>
 *     <li>Rights to interact with the file object are assigned.</li>
 *     <li>File object type {@link SearchFileObject}.</li>
 * </ul>
 */
public interface ServiceSearchFileObject extends CollectUniqueKeyRightUserFileObject {

    /**
     * Set system file object names to set the directory nesting level.
     * @param systemParents System names of file objects.
     * @return Class object.
     */
    ServiceSearchFileObject setSystemParents(Collection<String> systemParents);

    /**
     * Get system names of file objects.
     * @return System names of file objects.
     */
    Collection<String> getSystemParents();

    /**
     * Set the user login for which the search will be performed.
     * @param userLogin User login.
     * @return Class object.
     */
    ServiceSearchFileObject setUserLogin(String userLogin);

    /**
     * Get user login.
     * @return User login.
     */
    String getUserLogin();

    /**
     * Set part of the real name of the file object that will be searched for
     * @param partRealName Part of the real name of the file object.
     * @return Class object.
     */
    ServiceSearchFileObject setPartRealName(String partRealName);

    /**
     *  Get part of the real name of the file object.
     * @return Part of the real name of the file object.
     */
    String getPartRealName();

    /**
     * Set the file object type. Rule for searching file objects.
     * @param searchFileObject File object type.
     * @return Class object.
     */
    ServiceSearchFileObject setTypeSearchFileObject(SearchFileObject searchFileObject);

    /**
     * Get the file object type.
     * @return File object type.
     */
    SearchFileObject getTypeSearchFileObject();

    List<ContainerInfoSearchFileObject> search();

}
