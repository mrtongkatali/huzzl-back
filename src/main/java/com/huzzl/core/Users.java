package com.huzzl.core;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.jackson.JsonSnakeCase;
import org.hibernate.validator.constraints.Email;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
@Table(name="users")
@NamedQueries(
        {
                @NamedQuery( name = "Users.allUsers", query = "SELECT t FROM Task t"),
                @NamedQuery( name = "Users.findById", query = "SELECT t FROM Users t WHERE t.id = :id"),
                @NamedQuery( name = "Users.findByEmail", query = "SELECT t FROM Users t WHERE t.emailAddress = :email"),
        }
)

@JsonSnakeCase
@JsonIgnoreProperties(ignoreUnknown = true)
public class Users extends BaseEntity {

    @NotNull
    @Column(name="firstname")
    private String firstName;

    @NotNull
    @Column(name="lastname")
    private String lastName;

    @Size(min = 8, max = 25, message = "The length of password should be between 8 to 25")
    public transient String password;

    @NotNull
    @Email
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

    public Users(String email_address) {
        this.emailAddress = email_address;
    }

    @JsonProperty
    public String getFirstName() { return firstName; }

    @JsonProperty
    public String getLastName() { return lastName; }

    @JsonProperty
    public String getEmailAddress() { return emailAddress; }

    @JsonProperty
    public Integer status() { return status; }

    public String getPassword() { return password; }

    @OneToOne(fetch = FetchType.LAZY, mappedBy = "user")
    private UserLogin userlogin;




}
