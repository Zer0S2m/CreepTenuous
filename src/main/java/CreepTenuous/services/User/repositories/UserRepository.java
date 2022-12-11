package CreepTenuous.services.User.repositories;

import CreepTenuous.services.User.models.User;

import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<User, Long> {
    User findUserByLogin(String login);
    User findUserById(Long id);
}
