package CreepTenuous.api.core.security.jwt;

import CreepTenuous.api.core.version.v1.V1APIController;
import CreepTenuous.providers.jwt.http.JwtUserRequest;
import CreepTenuous.services.user.security.jwt.impl.JwtService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@V1APIController
@CrossOrigin
public class AuthController {
    @Autowired
    private JwtService jwtService;

    @PostMapping(value = "/auth/login")
    public void login(@RequestBody JwtUserRequest user) {
        jwtService.login(user);
    }

    @PostMapping(value = "/auth/token")
    public void access() {

    }

    @PostMapping(value = "/auth/refresh")
    public void refresh() {

    }
}
