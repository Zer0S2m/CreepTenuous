package com.zer0s2m.creeptenuous.repository.user;

import com.zer0s2m.creeptenuous.common.query.IFileObjectsExclusions;
import com.zer0s2m.creeptenuous.models.user.UserFileObjectsExclusion;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Repository
public interface UserFileObjectsExclusionRepository extends CrudRepository<UserFileObjectsExclusion, Long> {

    /**
     * Delete all entities by system names of file objects and user login
     *
     * @param fileSystemObject system names of file objects.
     *                         Must not be {@literal null} nor must it contain {@literal null}.
     * @param user_login       user login. Must not be {@literal null}.
     */
    void deleteAllByFileSystemObjectInAndUserLogin(Collection<UUID> fileSystemObject, String user_login);

    /**
     * Retrieves all file objects that are in exclusions and aggregates by users
     * @return entities
     */
    @Query(
            value = "SELECT u2.login AS userLogin, ARRAY_AGG(u.file_system_object) fileSystemObjects " +
                    "FROM user_file_objects_exclusions AS u " +
                    "INNER JOIN \"user\" u2 on u2.id = u.user_id " +
                    "GROUP BY u2.login",
            nativeQuery = true
    )
    List<IFileObjectsExclusions> getAllExclusionsAggregateUserId();

}
