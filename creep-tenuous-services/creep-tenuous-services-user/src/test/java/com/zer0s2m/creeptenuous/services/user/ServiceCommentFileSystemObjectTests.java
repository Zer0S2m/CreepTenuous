package com.zer0s2m.creeptenuous.services.user;

import com.zer0s2m.creeptenuous.common.containers.ContainerCommentFileSystemObject;
import com.zer0s2m.creeptenuous.common.enums.UserRole;
import com.zer0s2m.creeptenuous.common.exceptions.NotFoundCommentFileSystemObjectException;
import com.zer0s2m.creeptenuous.common.exceptions.UserNotFoundException;
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

import java.util.List;
import java.util.UUID;
import java.util.stream.StreamSupport;

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
    public void getList_successNotParent() {
        Assertions.assertDoesNotThrow(
                () -> serviceCommentFileSystemObject.list(UUID.randomUUID().toString(), "login")
        );
    }

    @Test
    public void getListAndCollect_success_byParents() {
        final User user = userRepository.save(RECORD_USER);
        final UUID systemName = UUID.randomUUID();

        CommentFileSystemObject comment1 = commentFileSystemObjectRepository.save(
                new CommentFileSystemObject(
                        user,
                        "Comment",
                        systemName,
                        null));
        CommentFileSystemObject comment2 = commentFileSystemObjectRepository.save(
                new CommentFileSystemObject(
                        user,
                        "Comment",
                        systemName,
                        comment1.getId()));
        commentFileSystemObjectRepository.save(
                new CommentFileSystemObject(
                        user,
                        "Comment",
                        systemName,
                        comment2.getId()));
        commentFileSystemObjectRepository.save(
                new CommentFileSystemObject(
                        user,
                        "Comment",
                        systemName,
                        null));

        List<CommentFileSystemObject> comments = Assertions.assertDoesNotThrow(
                () -> serviceCommentFileSystemObject.list(
                        systemName.toString(), user.getLogin())
        );
        Assertions.assertFalse(comments.isEmpty());

        Iterable<ContainerCommentFileSystemObject> commentsCollected = Assertions.assertDoesNotThrow(
                () -> serviceCommentFileSystemObject.collect(comments));
        List<ContainerCommentFileSystemObject> commentFileSystemObjectList = StreamSupport
                .stream(commentsCollected.spliterator(), false)
                .toList();

        Assertions.assertFalse(commentFileSystemObjectList.isEmpty());
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
    public void create_success_byIdUserAndParentIdComment() {
        final User user = userRepository.save(RECORD_USER);
        UUID systemName =  UUID.randomUUID();

        CommentFileSystemObject commentFileSystemObject = commentFileSystemObjectRepository.save(
                new CommentFileSystemObject(
                        user,
                        "Comment",
                        systemName,
                        null));

        CommentFileSystemObject createdCommentFileSystemObject = Assertions.assertDoesNotThrow(
                () -> serviceCommentFileSystemObject.create(
                        "Comment",
                        systemName.toString(),
                        commentFileSystemObject.getId(),
                        user.getId())
        );

        Assertions.assertEquals(
                createdCommentFileSystemObject.getParentId(),
                commentFileSystemObject.getId()
        );
    }

    @Test
    public void create_fail_byIdUserAndNotFoundParentIdComment() {
        final User user = userRepository.save(RECORD_USER);

        Assertions.assertThrows(
                NotFoundCommentFileSystemObjectException.class,
                () -> serviceCommentFileSystemObject.create(
                        "Comment",
                        UUID.randomUUID().toString(),
                        9999L,
                        user.getId())
        );
    }

    @Test
    public void create_fail_byIdUserNotFound() {
        Assertions.assertThrows(
                UserNotFoundException.class,
                () -> serviceCommentFileSystemObject.create(
                        "Comment",
                        UUID.randomUUID().toString(),
                        null,
                        99999L));
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
    public void create_fail_byLoginUserNotFound() {
        Assertions.assertThrows(
                UserNotFoundException.class,
                () -> serviceCommentFileSystemObject.create(
                        "Comment",
                        UUID.randomUUID().toString(),
                        null,
                        "login_user_not_found"));
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
