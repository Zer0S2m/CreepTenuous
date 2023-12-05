package com.zer0s2m.creeptenuous.module.search.api;

import com.zer0s2m.creeptenuous.common.annotations.V1APIRestController;
import com.zer0s2m.creeptenuous.module.search.ContainerInfoSearchFileObject;
import com.zer0s2m.creeptenuous.module.search.DataSearchFileObject;
import com.zer0s2m.creeptenuous.module.search.SearchFileObject;
import com.zer0s2m.creeptenuous.module.search.ServiceSearchFileObject;
import com.zer0s2m.creeptenuous.module.search.documentation.ControllerApiSearchFileObjectDoc;
import com.zer0s2m.creeptenuous.security.jwt.providers.JwtProvider;
import com.zer0s2m.creeptenuous.security.jwt.utils.JwtUtils;
import io.jsonwebtoken.Claims;
import jakarta.validation.Valid;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.List;

@V1APIRestController
public class ControllerApiSearchFileObject implements ControllerApiSearchFileObjectDoc {

    private final JwtProvider jwtProvider;

    private final ServiceSearchFileObject serviceSearchFileObject;

    public ControllerApiSearchFileObject(JwtProvider jwtProvider, ServiceSearchFileObject serviceSearchFileObject) {
        this.jwtProvider = jwtProvider;
        this.serviceSearchFileObject = serviceSearchFileObject;
    }

    /**
     * Search for file objects using the following criteria:
     * <ul>
     *     <li>File object type.</li>
     *     <li>Directory nesting level.</li>
     *     <li>Part of the real name of the file object.</li>
     *     <li>Rights to interact with the file object are assigned.</li>
     * </ul>
     *
     * @param data        Search data.
     * @param accessToken Raw JWT access token.
     * @return File objects found.
     */
    @Override
    @PostMapping("/module/search-file-object")
    @ResponseStatus(code = HttpStatus.OK)
    public List<ContainerInfoSearchFileObject> search(
            final @Valid @RequestBody @NotNull DataSearchFileObject data,
            @RequestHeader(name = "Authorization") String accessToken) {
        final Claims claims = jwtProvider.getAccessClaims(JwtUtils.getPureAccessToken(accessToken));

        return serviceSearchFileObject
                .setUserLogin(claims.get("login", String.class))
                .setSystemParents(data.systemParents())
                .setTypeSearchFileObject(SearchFileObject.valueOf(data.type()))
                .setPartRealName(data.partRealName())
                .search();
    }

}
