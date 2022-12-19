package CreepTenuous.services.User.repositories;

import CreepTenuous.services.User.models.User;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {
    User findUserByLogin(String login);
    User findUserByEmail(String email);
    Boolean existsUserByEmail(String email);
    Boolean existsUserByLogin(String email);
    User findUserById(Long id);
}
