package com.zer0s2m.creeptenuous.api.controllers.system;

import com.zer0s2m.creeptenuous.api.documentation.controllers.ControllerApiRenameFileSystemObjectDoc;
import com.zer0s2m.creeptenuous.common.annotations.V1APIRestController;
import com.zer0s2m.creeptenuous.common.data.DataRenameFileSystemObjectApi;
import com.zer0s2m.creeptenuous.common.http.ResponseRenameFileSystemObjectDoc;
import jakarta.validation.Valid;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@V1APIRestController
public class ControllerApiRenameFileSystemObject implements ControllerApiRenameFileSystemObjectDoc {

    @Override
    @PutMapping("/file-system-object/rename")
    @ResponseStatus(code = HttpStatus.OK)
    public ResponseRenameFileSystemObjectDoc rename(
            final @Valid @RequestBody @NotNull DataRenameFileSystemObjectApi data,
            @RequestHeader(name = "Authorization") String accessToken) {
        return new ResponseRenameFileSystemObjectDoc(data.systemName(), data.newRealName());
    }

}
