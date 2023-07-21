package com.zer0s2m.creeptenuous.api.controllers.user;

import com.zer0s2m.creeptenuous.api.documentation.controllers.ControllerApiCategoryUserDoc;
import com.zer0s2m.creeptenuous.common.annotations.V1APIRestController;
import com.zer0s2m.creeptenuous.common.containers.ContainerDataUserCategory;
import com.zer0s2m.creeptenuous.common.data.DataCreateUserCategoryApi;
import com.zer0s2m.creeptenuous.common.data.DataDeleteUserCategoryApi;
import com.zer0s2m.creeptenuous.common.data.DataEditUserCategoryApi;
import com.zer0s2m.creeptenuous.common.exceptions.NotFoundException;
import com.zer0s2m.creeptenuous.common.exceptions.UserNotFoundException;
import com.zer0s2m.creeptenuous.security.jwt.providers.JwtProvider;
import com.zer0s2m.creeptenuous.security.jwt.utils.JwtUtils;
import com.zer0s2m.creeptenuous.services.user.ServiceCategoryUser;
import io.jsonwebtoken.Claims;
import jakarta.validation.Valid;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@V1APIRestController
public class ControllerApiCategoryUser implements ControllerApiCategoryUserDoc {

    private final ServiceCategoryUser serviceCategoryUser;

    private final JwtProvider jwtProvider;

    @Autowired
    public ControllerApiCategoryUser(ServiceCategoryUser serviceCategoryUser, JwtProvider jwtProvider) {
        this.serviceCategoryUser = serviceCategoryUser;
        this.jwtProvider = jwtProvider;
    }

    @Override
    @GetMapping("/user/category")
    @ResponseStatus(code = HttpStatus.OK)
    public List<ContainerDataUserCategory> getAll(@RequestHeader(name = "Authorization") String accessToken) {
        final Claims claims = jwtProvider.getAccessClaims(JwtUtils.getPureAccessToken(accessToken));
        return serviceCategoryUser.getAll(claims.get("login", String.class));
    }

    /**
     * Creating a category for file objects
     * @param data data to create
     * @param accessToken raw access JWT token
     * @return entity data
     * @throws UserNotFoundException user not exists
     */
    @Override
    @PostMapping("/user/category")
    @ResponseStatus(code = HttpStatus.CREATED)
    public ContainerDataUserCategory create(
            final @Valid @RequestBody @NotNull DataCreateUserCategoryApi data,
            @RequestHeader(name = "Authorization") String accessToken) throws UserNotFoundException {
        final Claims claims = jwtProvider.getAccessClaims(JwtUtils.getPureAccessToken(accessToken));
        return serviceCategoryUser.create(data.title(), claims.get("login", String.class));
    }

    /**
     * Update custom category by id
     * @param data data to update
     * @param accessToken raw access JWT token
     * @throws NotFoundException not found user category
     */
    @Override
    @PutMapping("/user/category")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void edit(
            final @Valid @RequestBody @NotNull DataEditUserCategoryApi data,
            @RequestHeader(name = "Authorization") String accessToken) throws NotFoundException {
        final Claims claims = jwtProvider.getAccessClaims(JwtUtils.getPureAccessToken(accessToken));
        serviceCategoryUser.edit(data.id(), data.title(),
                claims.get("login", String.class));
    }

    /**
     * Delete a custom category by its id
     * @param data data to delete
     * @param accessToken raw access JWT token
     * @throws NotFoundException not found user category
     */
    @Override
    @DeleteMapping("/user/category")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void delete(
            final @Valid @RequestBody @NotNull DataDeleteUserCategoryApi data,
            @RequestHeader(name = "Authorization") String accessToken) throws NotFoundException {
        final Claims claims = jwtProvider.getAccessClaims(JwtUtils.getPureAccessToken(accessToken));
        serviceCategoryUser.delete(data.id(), claims.get("login", String.class));
    }

}
