package CreepTenuous.services.user.security.jwt;

import CreepTenuous.providers.jwt.http.JwtUserRequest;

public interface IJwtService {
    void login(JwtUserRequest user);
    void access();
    void refresh();
}
