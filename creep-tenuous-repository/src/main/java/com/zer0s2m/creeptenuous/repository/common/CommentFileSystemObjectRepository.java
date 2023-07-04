package com.zer0s2m.creeptenuous.repository.common;

import com.zer0s2m.creeptenuous.models.common.CommentFileSystemObject;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentFileSystemObjectRepository extends CrudRepository<CommentFileSystemObject, Long> {
}
