package com.zer0s2m.creeptenuous.services.user.impl;

import com.zer0s2m.creeptenuous.common.exceptions.UserNotFoundException;
import com.zer0s2m.creeptenuous.common.utils.OptionalMutable;
import com.zer0s2m.creeptenuous.models.user.User;
import com.zer0s2m.creeptenuous.redis.services.user.ServiceBlockUserRedis;
import com.zer0s2m.creeptenuous.repository.user.UserRepository;
import com.zer0s2m.creeptenuous.services.user.ServiceControlUser;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * Service for controlling users in the system.
 * <ul>
 *     <li>Getting all users on the system</li>
 *     <li>Removing a user from the system</li>
 *     <li>Account blocking</li>
 *     <li>Blocking a user for a while</li>
 * </ul>
 */
@Service("service-control-user")
public class ServiceControlUserImpl implements ServiceControlUser {

    private final UserRepository userRepository;

    private final ServiceBlockUserRedis serviceBlockUserRedis;

    @Autowired
    public ServiceControlUserImpl(UserRepository userRepository, ServiceBlockUserRedis serviceBlockUserRedis) {
        this.userRepository = userRepository;
        this.serviceBlockUserRedis = serviceBlockUserRedis;
    }

    /**
     * Getting all users on the system
     * @return users
     */
    @Override
    public List<User> getAllUsers() {
        return userRepository
                .findAll()
                .stream()
                .toList();
    }

    /**
     * Removing a user from the system by his login
     * @param login user login
     * @throws UserNotFoundException user does not exist in the system
     */
    @Override
    public void deleteUser(String login) throws UserNotFoundException {
        final User user = userRepository.findByLogin(login);
        if (user == null) {
            throw new UserNotFoundException();
        }
        userRepository.delete(user);
    }

    /**
     * Block a user in the system by his login
     * @param login user login
     * @throws UserNotFoundException user does not exist in the system
     */
    @Override
    public void blockUser(String login) throws UserNotFoundException {
        setActivityUser(login, false);
    }

    /**
     * Blocking a user by his login for a while
     * @param login user login
     * @param fromDate block start date
     * @param toDate lock end date
     * @throws UserNotFoundException user does not exist in the system
     */
    @Override
    public void blockUserTemporarily(String login, LocalDateTime fromDate, LocalDateTime toDate)
            throws UserNotFoundException {
        getUserAndCheckExists(login);

        OptionalMutable<LocalDateTime> cleanFromDate = new OptionalMutable<>(fromDate);
        if (cleanFromDate.getValue() == null) {
            long diff;
            cleanFromDate.setValue(LocalDateTime.now());
            diff = ChronoUnit.SECONDS.between(
                    ZonedDateTime.ofLocal(cleanFromDate.getValue(), ZoneId.of("UTC"), ZoneOffset.UTC),
                    ZonedDateTime.ofLocal(toDate, ZoneId.of("UTC"), ZoneOffset.UTC));
            serviceBlockUserRedis.block(diff, login);
        } else {
            long diff;
            long diffNowBetweenFrom = ChronoUnit.SECONDS.between(
                    ZonedDateTime.ofLocal(LocalDateTime.now(), ZoneId.of("UTC"), ZoneOffset.UTC),
                    ZonedDateTime.ofLocal(toDate, ZoneId.of("UTC"), ZoneOffset.UTC));
            long diffFromBetweenTo = ChronoUnit.SECONDS.between(
                    ZonedDateTime.ofLocal(fromDate, ZoneId.of("UTC"), ZoneOffset.UTC),
                    ZonedDateTime.ofLocal(toDate, ZoneId.of("UTC"), ZoneOffset.UTC));
            diff = diffNowBetweenFrom + diffFromBetweenTo;
            serviceBlockUserRedis.block(diff, login, true, diffNowBetweenFrom);
        }
    }

    /**
     * Unblock a user in the system by his login
     * @param login user login
     * @throws UserNotFoundException user does not exist in the system
     */
    @Override
    public void unblockUser(String login) throws UserNotFoundException {
        setActivityUser(login, true);
        serviceBlockUserRedis.unblock(login);
    }

    /**
     * Set user activity by login
     * @param login user login
     * @param activity is account non-locked
     * @throws UserNotFoundException user does not exist in the system
     */
    private void setActivityUser(String login, boolean activity) throws UserNotFoundException {
        User user = getUserAndCheckExists(login);
        user.setActivity(activity);
        userRepository.save(user);
    }

    /**
     * Check user for existence and return
     * @param login user login
     * @return user
     * @throws UserNotFoundException user does not exist in the system
     */
    private @NotNull User getUserAndCheckExists(String login) throws UserNotFoundException {
        final User user = userRepository.findByLogin(login);
        if (user == null) {
            throw new UserNotFoundException();
        }
        return user;
    }

}
