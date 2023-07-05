package com.zer0s2m.creeptenuous.repository.common;

import com.zer0s2m.creeptenuous.models.common.CommentFileSystemObject;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CommentFileSystemObjectRepository extends CrudRepository<CommentFileSystemObject, Long> {

    /**
     * Retrieves an object by its ID and username.
     * @param id id comment. Must not be {@literal null}.
     * @param user_login user login. Must not be {@literal null}.
     * @return comment
     */
    Optional<CommentFileSystemObject> findByIdAndUserLogin(Long id, String user_login);

    /**
     * Returns whether an entity exists with the given id and username.
     * @param id id comment. Must not be {@literal null}.
     * @param user_login user login. Must not be {@literal null}.
     * @return is exists
     */
    boolean existsByIdAndUserLogin(Long id, String user_login);

}
