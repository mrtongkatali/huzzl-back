package com.huzzl.core;

import io.dropwizard.jackson.JsonSnakeCase;
import io.swagger.annotations.ApiModelProperty;

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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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

    @ApiModelProperty(value = "Entity id", hidden = true)
    public Long getId() {return id; }

    @ApiModelProperty(value = "Date Added", hidden = true)
    public Date getDateAdded() { return date_added; }

    @ApiModelProperty(value = "Date last modified", hidden = true)
    public Date getLastModified() { return last_modified; }

    public void setId(Long id) {
        this.id = id;
    }

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
