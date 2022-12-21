package CreepTenuous.api.controllers.user.createUser.forms;

public class DataCreateUserApi {
    private final String login;
    private final String password;
    private final String email;
    private final String name;

    public DataCreateUserApi(String login, String password, String email, String name) {
        this.login = login;
        this.password = password;
        this.email = email;
        this.name = name;
    }

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }
}
