package com.zer0s2m.creeptenuous.repository.common;

import com.zer0s2m.creeptenuous.models.common.ShortcutFileSystemObject;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ShortcutFileSystemObjectRepository extends CrudRepository<ShortcutFileSystemObject, Long> {
}
