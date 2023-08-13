package com.zer0s2m.creeptenuous.api.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zer0s2m.creeptenuous.common.data.*;
import com.zer0s2m.creeptenuous.common.enums.UserRole;
import com.zer0s2m.creeptenuous.models.user.User;
import com.zer0s2m.creeptenuous.models.user.UserColor;
import com.zer0s2m.creeptenuous.models.user.UserColorDirectory;
import com.zer0s2m.creeptenuous.redis.models.DirectoryRedis;
import com.zer0s2m.creeptenuous.redis.models.FileRedis;
import com.zer0s2m.creeptenuous.redis.repository.DirectoryRedisRepository;
import com.zer0s2m.creeptenuous.redis.repository.FileRedisRepository;
import com.zer0s2m.creeptenuous.repository.user.UserColorDirectoryRepository;
import com.zer0s2m.creeptenuous.repository.user.UserColorRepository;
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

@Transactional
@Rollback
@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@TestTagControllerApi
public class ControllerApiCustomizationUserTests {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserColorDirectoryRepository userColorDirectoryRepository;

    @Autowired
    private DirectoryRedisRepository directoryRedisRepository;

    @Autowired
    private FileRedisRepository fileRedisRepository;

    @Autowired
    private UserColorRepository userColorRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    User RECORD_CREATE_USER = new User(
            UtilsAuthAction.LOGIN,
            "test_password",
            "test_admin@test_admin.com",
            "test_admin",
            UserRole.ROLE_USER
    );

    private final String accessToken = UtilsAuthAction.builderHeader(UtilsAuthAction.generateAccessToken());

    @Test
    public void setColorInDirectory_create_success() throws Exception {
        User user = userRepository.save(RECORD_CREATE_USER);
        UserColor userColor = userColorRepository.save(new UserColor(user, "color"));

        final UUID systemName = UUID.randomUUID();

        directoryRedisRepository.save(new DirectoryRedis(
                RECORD_CREATE_USER.getLogin(),
                "ROLE_USER",
                systemName.toString(),
                systemName.toString(),
                systemName.toString(),
                new ArrayList<>()
        ));

        this.mockMvc.perform(
                MockMvcRequestBuilders.put("/api/v1/user/customization/directory/color")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new DataCreateUserColorDirectoryApi(
                                systemName.toString(), userColor.getId()
                        )))
                        .header("Authorization", accessToken)
                )
                .andExpect(status().isNoContent());

        Assertions.assertTrue(userColorDirectoryRepository.findByUserLoginAndDirectory(
                RECORD_CREATE_USER.getLogin(), systemName
        ).isPresent());
    }

    @Test
    public void setColorInDirectory_edit_success() throws Exception {
        User user = userRepository.save(RECORD_CREATE_USER);
        UserColor userColor = userColorRepository.save(new UserColor(user, "color"));
        final UUID systemName = UUID.randomUUID();

        userColorDirectoryRepository.save(new UserColorDirectory(
                user, userColor, systemName));
        directoryRedisRepository.save(new DirectoryRedis(
                RECORD_CREATE_USER.getLogin(),
                "ROLE_USER",
                systemName.toString(),
                systemName.toString(),
                systemName.toString(),
                new ArrayList<>()
        ));

        this.mockMvc.perform(
                MockMvcRequestBuilders.put("/api/v1/user/customization/directory/color")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new DataCreateUserColorDirectoryApi(
                                systemName.toString(), userColor.getId()
                        )))
                        .header("Authorization", accessToken)
                )
                .andExpect(status().isNoContent());
    }

    @Test
    public void setColorInDirectory_fail_notFoundFileSystemObject() throws Exception {
        User user = userRepository.save(RECORD_CREATE_USER);
        UserColor userColor = userColorRepository.save(new UserColor(user, "color"));

        this.mockMvc.perform(
                MockMvcRequestBuilders.put("/api/v1/user/customization/directory/color")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new DataCreateUserColorDirectoryApi(
                                UUID.randomUUID().toString(), userColor.getId()
                        )))
                        .header("Authorization", accessToken)
                )
                .andExpect(status().isNotFound());
    }

    @Test
    public void setColorInDirectory_fail_fileObjectIsNotDirectoryType() throws Exception {
        User user = userRepository.save(RECORD_CREATE_USER);
        UserColor userColor = userColorRepository.save(new UserColor(user, "color"));

        final UUID systemName = UUID.randomUUID();

        fileRedisRepository.save(new FileRedis(
                RECORD_CREATE_USER.getLogin(),
                "ROLE_USER",
                systemName.toString(),
                systemName.toString(),
                systemName.toString(),
                new ArrayList<>()
        ));

        this.mockMvc.perform(
                MockMvcRequestBuilders.put("/api/v1/user/customization/directory/color")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new DataCreateUserColorDirectoryApi(
                                systemName.toString(), userColor.getId()
                        )))
                        .header("Authorization", accessToken)
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    public void deleteColorInDirectory_success() throws Exception {
        User user = userRepository.save(RECORD_CREATE_USER);
        UserColor userColor = userColorRepository.save(new UserColor(user, "color"));
        final UUID systemName = UUID.randomUUID();

        userColorDirectoryRepository.save(new UserColorDirectory(
                user, userColor, systemName));
        directoryRedisRepository.save(new DirectoryRedis(
                RECORD_CREATE_USER.getLogin(),
                "ROLE_USER",
                systemName.toString(),
                systemName.toString(),
                systemName.toString(),
                new ArrayList<>()
        ));

        this.mockMvc.perform(
                MockMvcRequestBuilders.delete("/api/v1/user/customization/directory/color")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new DataControlFileSystemObjectApi(
                                systemName.toString()
                        )))
                        .header("Authorization", accessToken)
                )
                .andExpect(status().isNoContent());

        Assertions.assertTrue(userColorDirectoryRepository.findByUserLoginAndDirectory(
                RECORD_CREATE_USER.getLogin(), systemName
        ).isEmpty());
    }

    @Test
    public void deleteColorInDirectory_fail_notFoundFileSystemObject() throws Exception {
        this.mockMvc.perform(
                MockMvcRequestBuilders.delete("/api/v1/user/customization/directory/color")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new DataControlFileSystemObjectApi(
                                UUID.randomUUID().toString()
                        )))
                        .header("Authorization", accessToken)
                )
                .andExpect(status().isNotFound());
    }

    @Test
    public void deleteColorInDirectory_fail_fileObjectIsNotDirectoryType() throws Exception {
        User user = userRepository.save(RECORD_CREATE_USER);
        UserColor userColor = userColorRepository.save(new UserColor(user, "color"));
        final UUID systemName = UUID.randomUUID();

        userColorDirectoryRepository.save(new UserColorDirectory(
                user, userColor, systemName));
        fileRedisRepository.save(new FileRedis(
                RECORD_CREATE_USER.getLogin(),
                "ROLE_USER",
                systemName.toString(),
                systemName.toString(),
                systemName.toString(),
                new ArrayList<>()
        ));

        this.mockMvc.perform(
                MockMvcRequestBuilders.delete("/api/v1/user/customization/directory/color")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new DataControlFileSystemObjectApi(
                                systemName.toString()
                        )))
                        .header("Authorization", accessToken)
                )
                .andExpect(status().isBadRequest());

        Assertions.assertTrue(userColorDirectoryRepository.findByUserLoginAndDirectory(
                user.getLogin(), systemName
        ).isPresent());
    }

    @Test
    public void createCustomColor_success() throws Exception {
        userRepository.save(RECORD_CREATE_USER);

        this.mockMvc.perform(
                MockMvcRequestBuilders.post("/api/v1/user/customization/color")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new DataCreateCustomColorApi(
                                "color"
                        )))
                        .header("Authorization", accessToken)
                )
                .andExpect(status().isNoContent());
    }

    @Test
    public void createCustomColor_fail_notFoundUser() throws Exception {
        this.mockMvc.perform(
                MockMvcRequestBuilders.post("/api/v1/user/customization/color")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new DataCreateCustomColorApi(
                                "color"
                        )))
                        .header("Authorization", accessToken)
                )
                .andExpect(status().isNotFound());
    }

    @Test
    public void editCustomColor_success() throws Exception {
        User user = userRepository.save(RECORD_CREATE_USER);
        UserColor userColor = userColorRepository.save(new UserColor(user, "color"));

        this.mockMvc.perform(
                MockMvcRequestBuilders.put("/api/v1/user/customization/color")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new DataEditCustomColorApi(
                                userColor.getId(), "color2"
                        )))
                        .header("Authorization", accessToken)
                )
                .andExpect(status().isNoContent());

        Assertions.assertEquals(
                "color2",
                userColorRepository.findById(userColor.getId()).get().getColor()
        );
    }

    @Test
    public void editCustomColor_fail_notFoundUserColor() throws Exception {
        this.mockMvc.perform(
                MockMvcRequestBuilders.put("/api/v1/user/customization/color")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new DataEditCustomColorApi(
                                123123L, "color"
                        )))
                        .header("Authorization", accessToken)
                )
                .andExpect(status().isNotFound());
    }

    @Test
    public void deleteCustomColor_success() throws Exception {
        User user = userRepository.save(RECORD_CREATE_USER);
        UserColor userColor = userColorRepository.save(new UserColor(user, "color"));

        this.mockMvc.perform(
                MockMvcRequestBuilders.delete("/api/v1/user/customization/color")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new DataControlAnyObjectApi(
                                userColor.getId()
                        )))
                        .header("Authorization", accessToken)
                )
                .andExpect(status().isNoContent());
    }

    @Test
    public void deleteCustomColor_fail_notFoundUserColor() throws Exception {
        this.mockMvc.perform(
                MockMvcRequestBuilders.delete("/api/v1/user/customization/color")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new DataControlAnyObjectApi(
                                123123L
                        )))
                        .header("Authorization", accessToken)
                )
                .andExpect(status().isNotFound());
    }

}
