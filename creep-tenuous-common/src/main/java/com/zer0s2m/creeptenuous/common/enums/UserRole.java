package com.zer0s2m.creeptenuous.common.enums;

import org.springframework.security.core.GrantedAuthority;

public enum UserRole implements GrantedAuthority {

    ROLE_ADMIN("ROLE_ADMIN"),


    ROLE_USER("ROLE_USER");

    private final String role;

    UserRole(String role) {
        this.role = role;
    }

    @Override
    public String getAuthority() {
        return role;
    }

}
