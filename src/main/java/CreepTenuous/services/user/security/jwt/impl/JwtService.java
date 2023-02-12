package CreepTenuous.services.user.security.jwt.impl;

import CreepTenuous.providers.jwt.JwtProvider;
import CreepTenuous.providers.jwt.http.JwtUserRequest;
import CreepTenuous.services.user.security.jwt.IJwtService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("jwt-service")
public class JwtService implements IJwtService {
    @Autowired
    private JwtProvider jwtProvider;

    @Override
    public void login(JwtUserRequest user) {
        jwtProvider.generateAccessToken(user);
    }

    @Override
    public void access() {

    }

    @Override
    public void refresh() {

    }
}
