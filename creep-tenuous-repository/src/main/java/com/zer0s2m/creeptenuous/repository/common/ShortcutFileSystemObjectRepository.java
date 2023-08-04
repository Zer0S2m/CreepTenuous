package com.zer0s2m.creeptenuous.repository.common;

import com.zer0s2m.creeptenuous.models.common.ShortcutFileSystemObject;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ShortcutFileSystemObjectRepository extends CrudRepository<ShortcutFileSystemObject, Long> {

    /**
     * Removes instance
     * @param attachedFileSystemObject attached file object
     * @param toAttachedFileSystemObject file object to be attached to
     * @param user_login user login
     */
    void deleteByAttachedFileSystemObjectAndToAttachedFileSystemObjectAndUserLogin(
            UUID attachedFileSystemObject, UUID toAttachedFileSystemObject, String user_login);

    /**
     * Returns all instances of type T with the given toAttachedFileSystemObject and username.
     * @param toAttachedFileSystemObject file object to be attached to
     * @param user_login user login
     * @return entities
     */
    Iterable<ShortcutFileSystemObject> getAllByToAttachedFileSystemObjectAndUserLogin(
            UUID toAttachedFileSystemObject, String user_login);

}
