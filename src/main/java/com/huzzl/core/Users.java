package com.huzzl.core;

import io.dropwizard.jackson.JsonSnakeCase;

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import java.util.Objects;

@Entity
@Table(name="users")

@JsonSnakeCase
public class Users extends BaseEntity {

}
