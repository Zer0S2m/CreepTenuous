package CreepTenuous.repositories;

import CreepTenuous.models.User;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findByLogin(String login);
    Boolean existsUserByEmail(String email);
    Boolean existsUserByLogin(String email);
}
