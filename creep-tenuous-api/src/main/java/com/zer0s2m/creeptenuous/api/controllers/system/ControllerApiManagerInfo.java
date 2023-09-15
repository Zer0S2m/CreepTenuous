package com.zer0s2m.creeptenuous.api.controllers.system;

import com.zer0s2m.creeptenuous.api.documentation.controllers.ControllerApiManagerInfoDoc;
import com.zer0s2m.creeptenuous.common.annotations.V1APIRestController;
import com.zer0s2m.creeptenuous.common.data.DataManagerInfoApi;
import com.zer0s2m.creeptenuous.common.http.ResponseManagerInfoApi;
import jakarta.validation.Valid;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.ArrayList;

@V1APIRestController
public class ControllerApiManagerInfo implements ControllerApiManagerInfoDoc {

    @Override
    @PostMapping("/file-system-object/info")
    @ResponseStatus(code = HttpStatus.OK)
    public ResponseManagerInfoApi getInfoFileObjectsBySystemNames(
            final @Valid @RequestBody @NotNull DataManagerInfoApi data,
            @RequestHeader(name = "Authorization")  String accessToken) {
        return new ResponseManagerInfoApi(new ArrayList<>());
    }

}
