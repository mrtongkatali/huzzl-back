package com.huzzl.core;

import io.dropwizard.jackson.JsonSnakeCase;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

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

    @Column(name="task_title")
    private String taskTitle;

    @Column(name="task_description")
    private String taskDescription;

    public Task() {}
    public Task(String taskTitle, String taskDescription) {
        this.taskTitle          = taskTitle;
        this.taskDescription    = taskDescription;
    }

    public String getTaskTitle() { return taskTitle; }
    public String getTaskDescription() { return taskDescription; }
}
