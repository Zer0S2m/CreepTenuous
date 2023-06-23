package com.zer0s2m.creeptenuous.repository.user;

import com.zer0s2m.creeptenuous.models.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
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
     * @return is exists
     */
    Boolean existsUserByEmail(String email);

    /**
     * Check the user for his existence by login
     * @param login Must not be {@literal null}.
     * @return is exists
     */
    Boolean existsUserByLogin(String login);

}
