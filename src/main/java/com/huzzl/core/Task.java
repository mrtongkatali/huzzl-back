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
@Table(name="task")
@NamedQueries(
    {
        @NamedQuery(
            name = "Task.allTask",
            query = "SELECT t FROM Task t"
        )
    }
)

@JsonSnakeCase
public class Task {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private long id = 0;

    @Column(name="task_title")
    private String taskTitle;

    @Column(name="task_description")
    private String taskDescription;

    @NotNull
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="date_added")
    protected Date dateAdded;

    @NotNull
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="last_modified")
    protected Date lastModified;

    public Task() {}

    public Task(String taskTitle, String taskDescription, Date dateAdded, Date lastModified) {
        this.taskTitle          = taskTitle;
        this.taskDescription    = taskDescription;
        this.dateAdded          = new Date();
        this.lastModified       = new Date();
    }

    public String getTaskTitle() { return taskTitle; }
    public String getTaskDescription() { return taskDescription; }
    public Date getDateAdded() { return dateAdded; }
    public Date getLastModified() { return lastModified; }

    @Override
    public boolean equals(Object o) {
        if(o == null) {
            return false;
        }

        if(getClass() != o.getClass()) {
            return false;
        }

        final Task other = (Task) o;
        return Objects.equals(this.id, other.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, taskTitle, taskDescription);
    }

}
