package CreepTenuous.services.User.CreateUser.services.impl;

import CreepTenuous.services.User.CreateUser.services.ICreateUser;
import CreepTenuous.services.User.models.User;
import CreepTenuous.services.User.repositories.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("create-user")
public class CreateUser implements ICreateUser {
    @Autowired
    private UserRepository userRepository;

    public Long create(User user) {
        userRepository.save(user);
        return user.getId();
    }
}
