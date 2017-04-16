package com.huzzl.core;

import io.dropwizard.jackson.JsonSnakeCase;
import org.hibernate.annotations.Fetch;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(name="user_login")
@NamedQueries(
        {
                @NamedQuery( name = "UsersLogin.allUsers", query = "SELECT t FROM UserLogin t"),
                @NamedQuery( name = "UsersLogin.findById", query = "SELECT t FROM UserLogin t WHERE t.id = :id"),
                @NamedQuery( name = "UsersLogin.findByEmailAddress",
                        query = "SELECT a FROM UserLogin a JOIN a.user as b WHERE b.emailAddress = :email"
                )
        }
)

@JsonSnakeCase
public class UserLogin extends BaseEntity {

    @JoinColumn(name="user_id")
    @OneToOne(fetch = FetchType.LAZY)
    public Users user;

    @NotNull
    @Column(name="password_hash", length = 255)
    private String passwordHash;

    public UserLogin() {}

    public UserLogin(String passwordHash, Users user) throws PasswordStorage.CannotPerformOperationException {

        PasswordStorage pass = new PasswordStorage();

        this.passwordHash = pass.createHash(passwordHash);
        this.user         = user;
    }

    public String getPasswordHash() { return passwordHash; }
    public Users getUser() { return user; }

}
