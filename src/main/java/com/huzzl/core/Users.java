package com.huzzl.core;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.jackson.JsonSnakeCase;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.Email;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

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
    @ApiModelProperty(value = "User's firstname", required = true)
    public String getFirstName() { return firstName; }

    @JsonProperty
    @ApiModelProperty(value = "Users's lastname", required = true)
    public String getLastName() { return lastName; }

    @JsonProperty
    @ApiModelProperty(value = "User's valid email address", required = true)
    public String getEmailAddress() { return emailAddress; }

    @JsonProperty
    @ApiModelProperty(value = "Status of task. 0 - Inactive , 1 - Active", required = true, allowableValues = "0,1")
    public Integer status() { return status; }

    @ApiModelProperty(value = "Strong password must contain atleast a number, uppercase and special characters. Minimum of 8 characters.", required = true)
    public String getPassword() { return password; }

    /**
     * Set the pasword to null so that it will not show on the response
     */
    public void setPassword(String password) {
        this.password = password;
    }

    @OneToOne(fetch = FetchType.LAZY, mappedBy = "user")
    private UserLogin userlogin;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "user")
    private List<Task> tasks;




}
