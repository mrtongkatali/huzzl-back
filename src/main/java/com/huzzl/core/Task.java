package com.huzzl.core;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.jackson.JsonSnakeCase;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(name="task")
@NamedQueries(
    {
        @NamedQuery( name = "Task.allTask", query = "SELECT t FROM Task t"),
        @NamedQuery( name = "Task.findById", query = "SELECT t FROM Task t WHERE t.id = :id")
    }
)

@JsonSnakeCase
public class Task extends BaseEntity {

    @JoinColumn(name="user_id")
    @OneToOne(fetch = FetchType.LAZY)
    public Users user;

    @NotNull
    @Column(name="task_title")
    private String taskTitle;

    @Column(name="task_description")
    private String taskDescription;

    @NotNull
    @Column(name="status")
    private Integer status;

    public Task() {}
    public Task(String taskTitle, String taskDescription, Integer status) {
        this.taskTitle          = taskTitle;
        this.taskDescription    = taskDescription;
        this.status             = status;
    }

    @JsonProperty
    public String getTaskTitle() { return taskTitle; }

    @JsonProperty
    public String getTaskDescription() { return taskDescription; }

    @JsonProperty
    public Integer status() { return status; }
}
