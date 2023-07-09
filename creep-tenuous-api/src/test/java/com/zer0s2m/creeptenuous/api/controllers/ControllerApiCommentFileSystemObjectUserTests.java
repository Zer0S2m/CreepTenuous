package com.zer0s2m.creeptenuous.api.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zer0s2m.creeptenuous.common.data.DataCreateCommentFileSystemObjectApi;
import com.zer0s2m.creeptenuous.common.data.DataDeleteCommentFileSystemObjectApi;
import com.zer0s2m.creeptenuous.common.data.DataEditCommentFileSystemObjectApi;
import com.zer0s2m.creeptenuous.common.enums.UserRole;
import com.zer0s2m.creeptenuous.models.common.CommentFileSystemObject;
import com.zer0s2m.creeptenuous.models.user.User;
import com.zer0s2m.creeptenuous.redis.models.DirectoryRedis;
import com.zer0s2m.creeptenuous.redis.repository.DirectoryRedisRepository;
import com.zer0s2m.creeptenuous.repository.common.CommentFileSystemObjectRepository;
import com.zer0s2m.creeptenuous.repository.user.UserRepository;
import com.zer0s2m.creeptenuous.starter.test.annotations.TestTagControllerApi;
import com.zer0s2m.creeptenuous.starter.test.helpers.UtilsAuthAction;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.UUID;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc(addFilters = false)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@TestTagControllerApi
public class ControllerApiCommentFileSystemObjectUserTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private DirectoryRedisRepository directoryRedisRepository;

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

    private final String accessToken = UtilsAuthAction.builderHeader(UtilsAuthAction.generateAccessToken());

    @Test
    public void getList_success() throws Exception {
        final String systemName = UUID.randomUUID().toString();

        directoryRedisRepository.save(new DirectoryRedis(
                UtilsAuthAction.LOGIN,
                UtilsAuthAction.ROLE_USER,
                systemName,
                systemName,
                systemName,
                new ArrayList<>()
        ));

        this.mockMvc.perform(
                MockMvcRequestBuilders.get("/api/v1/common/comment/file-system-object")
                        .contentType(MediaType.APPLICATION_JSON)
                        .queryParam("file", systemName)
                        .header("Authorization",  accessToken)
                )
                .andExpect(status().isOk());

        directoryRedisRepository.deleteById(systemName);
    }

    @Test
    public void getList_fail_notExists() throws Exception {
        this.mockMvc.perform(
                MockMvcRequestBuilders.get("/api/v1/common/comment/file-system-object")
                        .contentType(MediaType.APPLICATION_JSON)
                        .queryParam("file", "not_file_system_object_exists")
                        .header("Authorization",  accessToken)
                )
                .andExpect(status().isNotFound());
    }

    @Test
    @Rollback
    public void create_success() throws Exception {
        final String systemName = UUID.randomUUID().toString();

        directoryRedisRepository.save(new DirectoryRedis(
                UtilsAuthAction.LOGIN,
                UtilsAuthAction.ROLE_USER,
                systemName,
                systemName,
                systemName,
                new ArrayList<>()
        ));

        this.mockMvc.perform(
                MockMvcRequestBuilders.post("/api/v1/common/comment/file-system-object")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new DataCreateCommentFileSystemObjectApi(
                                "Comment",
                                systemName
                        )))
                        .header("Authorization",  accessToken)
                )
                .andExpect(status().isCreated());

        directoryRedisRepository.deleteById(systemName);
    }

    @Test
    public void create_fail_notExists() throws Exception {
        this.mockMvc.perform(
                MockMvcRequestBuilders.post("/api/v1/common/comment/file-system-object")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new DataCreateCommentFileSystemObjectApi(
                                "Comment",
                                        "not_found_file_system_object"
                        )))
                        .header("Authorization",  accessToken)
                )
                .andExpect(status().isNotFound());
    }

    @Test
    @Rollback
    public void delete_success() throws Exception {
        final User user = userRepository.save(RECORD_USER);
        CommentFileSystemObject commentFileSystemObject = new CommentFileSystemObject(
                user,
                "comment",
                UUID.randomUUID());
        commentFileSystemObject = commentFileSystemObjectRepository.save(commentFileSystemObject);

        this.mockMvc.perform(
                MockMvcRequestBuilders.delete("/api/v1/common/comment/file-system-object")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new DataDeleteCommentFileSystemObjectApi(
                                commentFileSystemObject.getId()
                        )))
                        .header("Authorization",  accessToken)
                )
                .andExpect(status().isNoContent());

        userRepository.delete(user);
        Assertions.assertFalse(commentFileSystemObjectRepository.existsById(commentFileSystemObject.getId()));
    }

    @Test
    public void delete_fail_notExists() throws Exception {
        this.mockMvc.perform(
                MockMvcRequestBuilders.delete("/api/v1/common/comment/file-system-object")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new DataDeleteCommentFileSystemObjectApi(
                                11111L
                        )))
                        .header("Authorization",  accessToken)
                )
                .andExpect(status().isNotFound());
    }

    @Test
    @Rollback
    public void edit_success() throws Exception {
        final User user = userRepository.save(RECORD_USER);
        CommentFileSystemObject commentFileSystemObject = new CommentFileSystemObject(
                user,
                "comment",
                UUID.randomUUID());
        commentFileSystemObject = commentFileSystemObjectRepository.save(commentFileSystemObject);

        this.mockMvc.perform(
                MockMvcRequestBuilders.put("/api/v1/common/comment/file-system-object")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new DataEditCommentFileSystemObjectApi(
                                commentFileSystemObject.getId(),
                                "New comment"
                        )))
                        .header("Authorization",  accessToken)
                )
                .andExpect(status().isOk());

        Assertions.assertEquals("New comment", commentFileSystemObjectRepository
                .findById(commentFileSystemObject.getId())
                .get()
                .getComment());
        userRepository.delete(user);
        commentFileSystemObjectRepository.delete(commentFileSystemObject);
    }

    @Test
    public void edit_fail_notExists() throws Exception {
        this.mockMvc.perform(
                MockMvcRequestBuilders.put("/api/v1/common/comment/file-system-object")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new DataEditCommentFileSystemObjectApi(
                                11111L,
                                "New comment"
                        )))
                        .header("Authorization",  accessToken)
                )
                .andExpect(status().isNotFound());
    }

}
