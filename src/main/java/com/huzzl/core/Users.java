package com.huzzl.core;

import io.dropwizard.jackson.JsonSnakeCase;

import java.util.Date;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Objects;

@Entity
@Table(name="users")
@NamedQueries(
        {
                @NamedQuery( name = "Users.allUsers", query = "SELECT t FROM Task t"),
                @NamedQuery( name = "Users.findById", query = "SELECT t FROM Users t WHERE t.id = :id")
        }
)

@JsonSnakeCase
public class Users extends BaseEntity {

    @Column(name="firstname")
    private String firstName;

    @Column(name="lastname")
    private String lastName;

    @Column(name="email_address")
    private String emailAddress;

    @Column(name="status")
    private Integer status;

    public Users() {}

    public Users(String firstname, String lastname, String email_address, Integer status) {
        this.firstName      = firstname;
        this.lastName       = lastName;
        this.emailAddress   = email_address;
        this.status         = status;
    }


}
