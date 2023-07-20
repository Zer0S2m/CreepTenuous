package com.zer0s2m.creeptenuous.api.controllers.user;

import com.zer0s2m.creeptenuous.api.documentation.controllers.ControllerApiCategoryUserDoc;
import com.zer0s2m.creeptenuous.common.annotations.V1APIRestController;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;

@V1APIRestController
public class ControllerApiCategoryUser implements ControllerApiCategoryUserDoc {

    @Override
    @GetMapping("/user/category")
    public void getAll() {

    }

    @Override
    @PostMapping("/user/category")
    public void create() {

    }

    @Override
    @PutMapping("/user/category")
    public void edit() {

    }

    @Override
    @DeleteMapping("/user/category")
    public void delete() {

    }

}
