package CreepTenuous.providers.jwt.http;

public class JwtUserRequest {
    private final String login;
    private final String password;

    public JwtUserRequest(String login, String password) {
        this.login = login;
        this.password = password;
    }

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }
}
