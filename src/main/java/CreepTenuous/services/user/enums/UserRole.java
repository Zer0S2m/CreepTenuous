package CreepTenuous.services.user.enums;

//import org.springframework.security.core.GrantedAuthority;

public enum UserRole  {
    ADMIN("ADMIN"),
    USER("USER");

    private final String role;

    UserRole(String role) {
        this.role = role;
    }

//    @Override
    public String getAuthority() {
        return role;
    }
}
