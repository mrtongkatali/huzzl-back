package com.huzzl.core;

import java.security.Principal;
import java.util.Objects;
import java.util.Set;

public class AuthUser implements Principal {

    private final String firstname;
    private final String lastname;
    private final String emailAddress;
    private final String roles;
    private Long id;

    public AuthUser(Long id, String firstname, String lastname, String email_address, String roles) {
        this.id             = id;
        this.firstname      = firstname;
        this.lastname       = lastname;
        this.emailAddress   = email_address;
        this.roles          = roles;
    }

    public Long getId() { return id; }

    public String getEmailAddress() { return emailAddress; }

    public String getRoles() { return roles; }

    @Override
    public String getName() {
        return firstname + " " + lastname;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        if (getClass() != obj.getClass()) {
            return false;
        }

        final AuthUser other = (AuthUser) obj;

        return Objects.equals(this.id, other.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : -1;
    }
}