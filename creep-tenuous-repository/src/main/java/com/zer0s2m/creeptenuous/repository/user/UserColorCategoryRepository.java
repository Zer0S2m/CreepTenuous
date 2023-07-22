package com.zer0s2m.creeptenuous.repository.user;

import com.zer0s2m.creeptenuous.models.user.UserColorCategory;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserColorCategoryRepository extends CrudRepository<UserColorCategory, Long> {
}
