package com.zer0s2m.creeptenuous.services.user;

import com.zer0s2m.creeptenuous.common.enums.UserRole;
import com.zer0s2m.creeptenuous.common.exceptions.NotFoundCommentFileSystemObjectException;
import com.zer0s2m.creeptenuous.models.common.CommentFileSystemObject;
import com.zer0s2m.creeptenuous.models.user.User;
import com.zer0s2m.creeptenuous.repository.common.CommentFileSystemObjectRepository;
import com.zer0s2m.creeptenuous.repository.user.UserRepository;
import com.zer0s2m.creeptenuous.services.user.impl.ServiceCommentFileSystemObjectImpl;
import com.zer0s2m.creeptenuous.starter.test.annotations.TestTagService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@SpringBootTest(classes = {
        ServiceCommentFileSystemObjectImpl.class,
        UserRepository.class,
        CommentFileSystemObjectRepository.class
})
@Transactional
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@TestTagService
@ContextConfiguration(classes = { ConfigServices.class })
@Rollback
public class ServiceCommentFileSystemObjectTests {

    @Autowired
    private ServiceCommentFileSystemObject serviceCommentFileSystemObject;

    @Autowired
    private CommentFileSystemObjectRepository commentFileSystemObjectRepository;

    @Autowired
    private UserRepository userRepository;

    User RECORD_USER = new User(
            "test_login",
            "test_password",
            "test_login@test_login.com",
            "test_login",
            UserRole.ROLE_USER
    );

    @Test
    public void getList_success() {
        Assertions.assertDoesNotThrow(
                () -> serviceCommentFileSystemObject.list(UUID.randomUUID().toString(), "login")
        );
    }

    @Test
    public void create_success_byIdUser() {
        final User user = userRepository.save(RECORD_USER);

        Assertions.assertDoesNotThrow(
                () -> serviceCommentFileSystemObject.create(
                        "Comment",
                        UUID.randomUUID().toString(),
                        null,
                        user.getId())
        );
    }

    @Test
    public void create_success_byLoginUser() {
        final User user = userRepository.save(RECORD_USER);

        Assertions.assertDoesNotThrow(
                () -> serviceCommentFileSystemObject.create(
                        "Comment",
                        UUID.randomUUID().toString(),
                        null,
                        user.getLogin())
        );
    }

    @Test
    public void delete_success() {
        final User user = userRepository.save(RECORD_USER);
        final CommentFileSystemObject commentFileSystemObject =
                commentFileSystemObjectRepository.save(new CommentFileSystemObject(
                        user,
                        "comment",
                        UUID.randomUUID(),
                        null));

        Assertions.assertDoesNotThrow(
                () -> serviceCommentFileSystemObject.delete(commentFileSystemObject.getId(), user.getLogin())
        );
        Assertions.assertFalse(commentFileSystemObjectRepository.existsById(commentFileSystemObject.getId()));
    }

    @Test
    public void delete_fail_not_exists() {
        Assertions.assertThrows(
                NotFoundCommentFileSystemObjectException.class,
                () -> serviceCommentFileSystemObject.delete(1111111L, "login")
        );
    }

    @Test
    public void edit_success() {
        final User user = userRepository.save(RECORD_USER);
        final CommentFileSystemObject commentFileSystemObject =
                commentFileSystemObjectRepository.save(new CommentFileSystemObject(
                        user,
                        "comment",
                        UUID.randomUUID(),
                        null));

        Assertions.assertDoesNotThrow(
                () -> serviceCommentFileSystemObject.edit(
                        "New comment", commentFileSystemObject.getId(), user.getLogin())
        );
    }

    @Test
    public void edit_fail_notExists() {
        Assertions.assertThrows(
                NotFoundCommentFileSystemObjectException.class,
                () -> serviceCommentFileSystemObject.edit("New comment", 1111111L, "login")
        );
    }

}
