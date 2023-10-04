package com.zer0s2m.creeptenuous.repository.user;

import com.zer0s2m.creeptenuous.models.user.User;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Get user by username
     * @param login Must not be {@literal null}.
     * @return user
     */
    User findByLogin(String login);

    /**
     * Check the user for his existence by mail
     * @param email Must not be {@literal null}.
     * @return is existing
     */
    Boolean existsUserByEmail(String email);

    /**
     * Check the user for his existence by login
     * @param login Must not be {@literal null}.
     * @return is existing
     */
    Boolean existsUserByLogin(String login);

    /**
     * Update the avatar for the user based on his login.
     * @param avatar New user avatar.
     * @param login Must not be {@literal null}.
     */
    @Query(
            nativeQuery = true,
            value = "UPDATE \"user\" AS u SET avatar = ?1 WHERE u.login = ?2"
    )
    @Modifying
    @Transactional
    void updateAvatar(String avatar, String login);

}
