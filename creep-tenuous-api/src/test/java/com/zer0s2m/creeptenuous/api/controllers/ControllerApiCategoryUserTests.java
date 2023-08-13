package com.zer0s2m.creeptenuous.api.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zer0s2m.creeptenuous.common.data.DataControlFileSystemObjectInCategoryApi;
import com.zer0s2m.creeptenuous.common.data.DataCreateUserCategoryApi;
import com.zer0s2m.creeptenuous.common.data.DataControlAnyObjectApi;
import com.zer0s2m.creeptenuous.common.data.DataEditUserCategoryApi;
import com.zer0s2m.creeptenuous.common.enums.UserRole;
import com.zer0s2m.creeptenuous.models.user.CategoryFileSystemObject;
import com.zer0s2m.creeptenuous.models.user.User;
import com.zer0s2m.creeptenuous.models.user.UserCategory;
import com.zer0s2m.creeptenuous.redis.models.DirectoryRedis;
import com.zer0s2m.creeptenuous.redis.repository.DirectoryRedisRepository;
import com.zer0s2m.creeptenuous.repository.user.CategoryFileSystemObjectRepository;
import com.zer0s2m.creeptenuous.repository.user.UserCategoryRepository;
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
public class ControllerApiCategoryUserTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserCategoryRepository userCategoryRepository;

    @Autowired
    private DirectoryRedisRepository directoryRedisRepository;

    @Autowired
    private CategoryFileSystemObjectRepository categoryFileSystemObjectRepository;

    User RECORD_CREATE_USER = new User(
            UtilsAuthAction.LOGIN,
            "test_password",
            "test_admin@test_admin.com",
            "test_admin",
            UserRole.ROLE_USER
    );

    private final String accessToken = UtilsAuthAction.builderHeader(UtilsAuthAction.generateAccessToken());

    @Test
    @Rollback
    public void getAll_success() throws Exception {
        this.mockMvc.perform(
                MockMvcRequestBuilders.get("/api/v1/user/category")
                        .accept(MediaType.APPLICATION_JSON)
                        .header("Authorization", accessToken)
                )
                .andExpect(status().isOk());
    }

    @Test
    @Rollback
    public void create_success() throws Exception {
        userRepository.save(RECORD_CREATE_USER);

        this.mockMvc.perform(
                MockMvcRequestBuilders.post("/api/v1/user/category")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new DataCreateUserCategoryApi(
                                "Title"
                        )))
                        .header("Authorization", accessToken)
                )
                .andExpect(status().isCreated());
    }

    @Test
    @Rollback
    public void edit_success() throws Exception {
        User user = userRepository.save(RECORD_CREATE_USER);
        UserCategory userCategory = userCategoryRepository.save(new UserCategory(
                "Title", user));

        this.mockMvc.perform(
                MockMvcRequestBuilders.put("/api/v1/user/category")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new DataEditUserCategoryApi(
                                userCategory.getId(), "Title"
                        )))
                        .header("Authorization", accessToken)
                )
                .andExpect(status().isNoContent());
    }

    @Test
    @Rollback
    public void edit_fail_notFoundCategory() throws Exception {
        this.mockMvc.perform(
                MockMvcRequestBuilders.put("/api/v1/user/category")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new DataEditUserCategoryApi(
                                2343434L, "Title"
                        )))
                        .header("Authorization", accessToken)
                )
                .andExpect(status().isNotFound());
    }

    @Test
    @Rollback
    public void delete_success() throws Exception {
        User user = userRepository.save(RECORD_CREATE_USER);
        UserCategory userCategory = userCategoryRepository.save(new UserCategory(
                "Title", user));

        this.mockMvc.perform(
                MockMvcRequestBuilders.delete("/api/v1/user/category")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new DataControlAnyObjectApi(
                                userCategory.getId()
                        )))
                        .header("Authorization", accessToken)
                )
                .andExpect(status().isNoContent());

        Assertions.assertFalse(userCategoryRepository.existsById(userCategory.getId()));
    }

    @Test
    public void delete_fail_notFoundCategory() throws Exception {
        this.mockMvc.perform(
                MockMvcRequestBuilders.delete("/api/v1/user/category")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new DataControlAnyObjectApi(
                                123123L
                        )))
                        .header("Authorization", accessToken)
                )
                .andExpect(status().isNotFound());
    }

    @Test
    @Rollback
    public void setFileSystemObjectInCategory_success() throws Exception {
        User user = userRepository.save(RECORD_CREATE_USER);
        UserCategory userCategory = userCategoryRepository.save(new UserCategory(
                "Title", user));
        DirectoryRedis directoryRedis = directoryRedisRepository.save(new DirectoryRedis(
                UtilsAuthAction.LOGIN,
                UtilsAuthAction.ROLE_USER,
                "test",
                UUID.randomUUID().toString(),
                "test",
                new ArrayList<>()
        ));


        this.mockMvc.perform(
                MockMvcRequestBuilders.post("/api/v1/user/category/file-system-object")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new DataControlFileSystemObjectInCategoryApi(
                                directoryRedis.getSystemNameDirectory(), userCategory.getId()
                        )))
                        .header("Authorization", accessToken)
                )
                .andExpect(status().isNoContent());

        directoryRedisRepository.delete(directoryRedis);
    }

    @Test
    public void setFileSystemObjectInCategory_fail_notFoundFileSystemObject() throws Exception {
        this.mockMvc.perform(
                MockMvcRequestBuilders.post("/api/v1/user/category/file-system-object")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new DataControlFileSystemObjectInCategoryApi(
                                "not_found_file_object_system", 123L
                        )))
                        .header("Authorization", accessToken)
                )
                .andExpect(status().isNotFound());
    }

    @Test
    @Rollback
    public void unsetFileSystemObjectInCategory_success() throws Exception {
        User user = userRepository.save(RECORD_CREATE_USER);
        UserCategory userCategory = userCategoryRepository.save(new UserCategory(
                "Title", user));
        DirectoryRedis directoryRedis = directoryRedisRepository.save(new DirectoryRedis(
                UtilsAuthAction.LOGIN,
                UtilsAuthAction.ROLE_USER,
                "test",
                UUID.randomUUID().toString(),
                "test",
                new ArrayList<>()
        ));
        categoryFileSystemObjectRepository.save(new CategoryFileSystemObject(
                user, userCategory, UUID.fromString(directoryRedis.getSystemNameDirectory())
        ));

        this.mockMvc.perform(
                MockMvcRequestBuilders.delete("/api/v1/user/category/file-system-object")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new DataControlFileSystemObjectInCategoryApi(
                                directoryRedis.getSystemNameDirectory(), userCategory.getId()
                        )))
                        .header("Authorization", accessToken)
                )
                .andExpect(status().isNoContent());

        directoryRedisRepository.delete(directoryRedis);
    }

    @Test
    public void unsetFileSystemObjectInCategory_fail_notFoundFileSystemObject() throws Exception {
        this.mockMvc.perform(
                MockMvcRequestBuilders.delete("/api/v1/user/category/file-system-object")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new DataControlFileSystemObjectInCategoryApi(
                                "not_found_file_object_system", 123L
                        )))
                        .header("Authorization", accessToken)
                )
                .andExpect(status().isNotFound());
    }

    @Test
    @Rollback
    public void getFileSystemObjectInCategoryByCategoryId_success() throws Exception {
        User user = userRepository.save(RECORD_CREATE_USER);
        UserCategory userCategory = userCategoryRepository.save(new UserCategory(
                "Title", user));

        this.mockMvc.perform(
                MockMvcRequestBuilders.get("/api/v1/user/category/file-system-object")
                        .accept(MediaType.APPLICATION_JSON)
                        .queryParam("categoryId", userCategory.getId().toString())
                        .header("Authorization", accessToken)
                )
                .andExpect(status().isOk());
    }

}
