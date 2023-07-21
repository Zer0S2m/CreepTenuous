package com.zer0s2m.creeptenuous.api.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zer0s2m.creeptenuous.common.data.DataCreateUserCategoryApi;
import com.zer0s2m.creeptenuous.common.data.DataDeleteUserCategoryApi;
import com.zer0s2m.creeptenuous.common.data.DataEditUserCategoryApi;
import com.zer0s2m.creeptenuous.common.enums.UserRole;
import com.zer0s2m.creeptenuous.models.user.User;
import com.zer0s2m.creeptenuous.models.user.UserCategory;
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
                        .content(objectMapper.writeValueAsString(new DataDeleteUserCategoryApi(
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
                        .content(objectMapper.writeValueAsString(new DataDeleteUserCategoryApi(
                                123123L
                        )))
                        .header("Authorization", accessToken)
                )
                .andExpect(status().isNotFound());
    }

}
