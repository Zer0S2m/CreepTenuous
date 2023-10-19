package com.zer0s2m.creeptenuous.models.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.zer0s2m.creeptenuous.common.enums.UserRole;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "\"user\"")
public class User implements UserDetails {

    @Getter
    @Setter
    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    @JsonProperty
    private UserRole role;

    @Getter
    @Setter
    @Id
    @Column(name = "id")
    @SequenceGenerator(name = "UserSequence", sequenceName = "\"user_id_seq\"", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "UserSequence")
    private Long id;

    @Getter
    @Setter
    @JsonProperty
    @Basic
    @Column(name = "login", nullable = false, unique = true)
    private String login;

    @Getter
    @Setter
    @Column(name = "avatar")
    private String avatar;

    @Getter
    @Setter
    @JsonProperty
    @Basic
    @Column(name = "email")
    private String email;

    @Getter
    @Setter
    @JsonProperty
    @Basic
    @Column(name = "password", nullable = false)
    private String password;

    @Getter
    @Setter
    @JsonProperty
    @Basic
    @Column(name = "name")
    private String name;

    @Getter
    @Setter
    @Basic
    @CreationTimestamp
    @Column(name = "date_of_birth")
    private Date dateOfBrith;

    @Getter
    @Setter
    @Column(name = "activity")
    private boolean activity;

    @Getter
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private UserSettings userSettings;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<UserCategory> userCategories;

    public User(String login, String password, String email, String name) {
        this.password = password;
        this.login = login;
        this.email = email;
        this.name = name;
        this.setRole(UserRole.ROLE_USER);
        this.setActivity(true);
    }

    public User(String login, String password, String email, String name, UserRole role) {
        this(login, password, email, name);
        this.setRole(role);
        this.setActivity(true);
    }

    public User() {}

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    @Override
    public String getUsername() {
        return getLogin();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return activity;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

}
