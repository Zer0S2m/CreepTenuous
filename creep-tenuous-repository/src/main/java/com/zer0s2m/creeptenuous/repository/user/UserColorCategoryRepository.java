package com.zer0s2m.creeptenuous.repository.user;

import com.zer0s2m.creeptenuous.models.user.UserColorCategory;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserColorCategoryRepository extends CrudRepository<UserColorCategory, Long> {

    /**
     * Delete entity by ID user color, ID user category and user login
     * @param userColor_id    ID entity {@link com.zer0s2m.creeptenuous.models.user.UserColor}.
     *                        Must not be {@literal null}.
     * @param userCategory_id ID entity {@link com.zer0s2m.creeptenuous.models.user.UserCategory}.
     *                        Must not be {@literal null}.
     * @param user_login      user login. Must not be {@literal null}.
     * @return count deleted objects
     */
    long deleteByUserColorIdAndUserCategoryIdAndUserLogin(
            Long userColor_id, Long userCategory_id, String user_login);

}
