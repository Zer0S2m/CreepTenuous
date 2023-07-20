package com.zer0s2m.creeptenuous.models.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.zer0s2m.creeptenuous.common.enums.UserRole;
import jakarta.persistence.*;
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

    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    @JsonProperty
    private UserRole role;

    @Id
    @Column(name = "id")
    @SequenceGenerator(name = "UserSequence", sequenceName = "user_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "UserSequence")
    private Long id;

    @JsonProperty
    @Basic
    @Column(name = "login", nullable = false, unique = true)
    private String login;

    @JsonProperty
    @Basic
    @Column(name = "email")
    private String email;

    @JsonProperty
    @Basic
    @Column(name = "password", nullable = false)
    private String password;

    @JsonProperty
    @Basic
    @Column(name = "name")
    private String name;

    @Basic
    @CreationTimestamp
    @Column(name = "date_of_birth")
    private Date dateOfBrith;

    @Column(name = "activity")
    private boolean activity;

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

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return getLogin();
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
}

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

    public void setActivity(boolean activity) {
        this.activity = activity;
    }

    public Date getDateOfBrith() {
        return dateOfBrith;
    }

    public void setDateOfBrith(Date dateOfBrith) {
        this.dateOfBrith = dateOfBrith;
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
