package com.huzzl.core;

import io.dropwizard.jackson.JsonSnakeCase;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

@MappedSuperclass
@JsonSnakeCase
public class BaseEntity implements Serializable  {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    protected Long id;

    @NotNull
    @Temporal(TemporalType.TIMESTAMP)
    protected Date date_added;

    @NotNull
    @Temporal(TemporalType.TIMESTAMP)
    protected Date last_modified;

    public BaseEntity() {
        this.date_added      = new Date();
        this.last_modified   = new Date();
    }

    public Long getId() {return id; }
    public Date getDateAdded() { return date_added; }
    public Date getLastModified() { return last_modified; }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : -1;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        if (getClass() != obj.getClass()) {
            return false;
        }

        final BaseEntity other = (BaseEntity) obj;

        return Objects.equals(this.id, other.id);
    }

}
