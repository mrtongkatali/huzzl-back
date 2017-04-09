package com.huzzl.core;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.jackson.JsonSnakeCase;
import org.hibernate.validator.constraints.Email;

import java.util.Date;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Objects;

@Entity
@Table(name="users")
@NamedQueries(
        {
                @NamedQuery( name = "Users.allUsers", query = "SELECT t FROM Task t"),
                @NamedQuery( name = "Users.findById", query = "SELECT t FROM Users t WHERE t.id = :id"),
                @NamedQuery( name = "Users.findByEmail", query = "SELECT t FROM Users t WHERE t.emailAddress = :email")
        }
)

@JsonSnakeCase
public class Users extends BaseEntity {

    @NotNull
    @Column(name="firstname")
    private String firstName;

    @NotNull
    @Column(name="lastname")
    private String lastName;

    @NotNull
    @Column(name="email_address", unique = true)
    private String emailAddress;

    @Column(name="status")
    private Integer status;

    public Users() {}

    public Users(String firstname, String lastname, String email_address, Integer status) {
        this.firstName      = firstname;
        this.lastName       = lastname;
        this.emailAddress   = email_address;
        this.status         = status;
    }

    @JsonProperty
    public String getFirstName() { return firstName; }

    @JsonProperty
    public String getLastName() { return lastName; }

    @JsonProperty
    @Email
    public String getEmailAddress() { return emailAddress; }

    @JsonProperty
    public Integer status() { return status; }


}
