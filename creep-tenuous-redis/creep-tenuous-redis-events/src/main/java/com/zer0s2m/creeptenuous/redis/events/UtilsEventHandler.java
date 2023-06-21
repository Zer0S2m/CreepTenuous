package com.zer0s2m.creeptenuous.redis.events;

import com.zer0s2m.creeptenuous.redis.repository.JwtRedisRepository;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Event handler tool
 */
interface UtilsEventHandler {

    /**
     * Get all usernames in the system
     * @param jwtRedisRepository repository
     * @return user logins
     */
    static @NotNull List<String> getUserLogins(@NotNull JwtRedisRepository jwtRedisRepository) {
        List<String> userLogins = new ArrayList<>();
        jwtRedisRepository.findAll().forEach(entity -> userLogins.add(entity.getLogin()));
        return userLogins;
    }

}
