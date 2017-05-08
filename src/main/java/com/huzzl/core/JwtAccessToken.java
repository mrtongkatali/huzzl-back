package com.huzzl.core;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.jackson.JsonSnakeCase;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Entity
@Table(name="jwt_access_token")
@NamedQueries(
        {
                @NamedQuery( name = "JWT.allToken", query = "SELECT jwt FROM JwtAccessToken jwt"),
                @NamedQuery( name = "JWT.findById", query = "SELECT jwt FROM JwtAccessToken jwt WHERE jwt.id = :id")
        }
)

@JsonSnakeCase
public class JwtAccessToken extends BaseEntity {

    @JoinColumn(name="user_id")
    @ManyToOne(fetch = FetchType.LAZY)
    public Users user;

    @NotNull
    @Column(name="token")
    private String token;

    @NotNull
    @Column(name="expires")
    private Date expires;


    public JwtAccessToken(String token, Date expires, Users user) {
        this.token    = token;
        this.expires  = expires;
        this.user     = user;
    }

    @JsonProperty
    public String getToken() { return token; }

    @JsonProperty
    public Date getExpires() { return expires; }

    @JsonIgnore
    public Users getUser() { return user; }
}
