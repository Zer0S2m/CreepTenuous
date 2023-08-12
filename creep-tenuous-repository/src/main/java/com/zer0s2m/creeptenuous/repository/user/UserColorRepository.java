package com.zer0s2m.creeptenuous.repository.user;

import com.zer0s2m.creeptenuous.models.user.UserColor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserColorRepository extends CrudRepository<UserColor, Long> {

    Optional<UserColor> findByIdAndUserLogin(Long id, String user_login);

}
