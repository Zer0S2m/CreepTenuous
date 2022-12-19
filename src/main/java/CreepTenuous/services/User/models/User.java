package CreepTenuous.services.User.models;

import CreepTenuous.services.User.GeneratePassword.services.impl.GeneratePassword;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "\"user\"")
public class User implements Serializable {
    @Id
    @Column(name = "id")
    @SequenceGenerator(name = "UserSequence", sequenceName = "user_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "UserSequence")
    private Long id;

    @JsonProperty
    @Basic
    @Column(name = "login", nullable = false)
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

    public User(String login, String password, String email, String name) {
        GeneratePassword generatePassword = new GeneratePassword();

        this.password = generatePassword.generation(password);
        this.login = login;
        this.email = email;
        this.name = name;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        GeneratePassword generatePassword = new GeneratePassword();
        this.password = generatePassword.generation(password);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
}

    public Date getDateOfBrith() {
        return dateOfBrith;
    }

    public void setDateOfBrith(Date dateOfBrith) {
        this.dateOfBrith = dateOfBrith;
    }
}