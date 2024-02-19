package com.zer0s2m.creeptenuous.services.redis.user;

import com.zer0s2m.creeptenuous.redis.models.BlockUserDelayedRedis;
import com.zer0s2m.creeptenuous.redis.models.BlockUserRedis;
import com.zer0s2m.creeptenuous.redis.repository.BlockUserDelayedRedisRepository;
import com.zer0s2m.creeptenuous.redis.repository.BlockUserRedisRepository;
import com.zer0s2m.creeptenuous.redis.services.user.ServiceBlockUserRedis;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Service for blocking and unblocking a user for a period of time
 */
@Service("service-block-user-redis")
public class ServiceBlockUserRedisImpl implements ServiceBlockUserRedis {

    private final BlockUserRedisRepository blockUserRedisRepository;

    private final BlockUserDelayedRedisRepository blockUserDelayedRedisRepository;

    @Autowired
    public ServiceBlockUserRedisImpl(
            BlockUserRedisRepository blockUserRedisRepository,
            BlockUserDelayedRedisRepository blockUserDelayedRedisRepository) {
        this.blockUserRedisRepository = blockUserRedisRepository;
        this.blockUserDelayedRedisRepository = blockUserDelayedRedisRepository;
    }

    /**
     * Block a user for a period of time
     * @param expiration block expiration
     * @param login user login
     * @param isDelayed Whether the lock is delayed
     * @param delayed block expiration delayed
     */
    @Override
    public void block(Long expiration, String login, boolean isDelayed, Long delayed) {
        if (isDelayed) {
            blockUserDelayedRedisRepository.save(new BlockUserDelayedRedis(login, delayed));
        }

        blockUserRedisRepository.save(new BlockUserRedis(login, expiration));
    }

    /**
     * Unblock user
     * @param login user login
     */
    @Override
    public void unblock(String login) {
        blockUserRedisRepository.deleteById(login);
        blockUserDelayedRedisRepository.deleteById(login);
    }

    /**
     * Check if account is blocked
     * @param login user login
     * @return is non account blocked
     */
    @Override
    public boolean check(String login) {
        return blockUserDelayedRedisRepository.existsById(login) || blockUserRedisRepository.existsById(login);
    }

}
