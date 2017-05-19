package com.huzzl.core;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.jackson.JsonSnakeCase;
import io.swagger.annotations.ApiModelProperty;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(name="task")
@NamedQueries(
    {
        @NamedQuery( name = "Task.allTask", query = "SELECT t FROM Task t"),
        @NamedQuery( name = "Task.findById", query = "SELECT t FROM Task t WHERE t.id = :id"),
        @NamedQuery( name = "Task.getAllTaskByUserId", query = "SELECT t FROM Task t WHERE t.user.id = :user AND t.status = :status ORDER BY sort DESC"),
        @NamedQuery( name = "Task.countAllTaskByUserId", query = "SELECT count(t.id) FROM Task t WHERE t.user.id = :user AND t.status = :status")

    }
)

@JsonSnakeCase
public class Task extends BaseEntity {

    @JoinColumn(name="user_id")
    @ManyToOne(fetch = FetchType.LAZY)
    public Users user;

    @NotNull
    @Column(name="task_title")
    private String taskTitle;

    @Column(name="task_description")
    private String taskDescription;

    @Column(name="sort")
    private Integer sort;

    @NotNull
    @Column(name="status")
    private Integer status;

    public Task() {}

    public Task(String taskTitle, String taskDescription, Integer status, Integer sort, Users user) {
        this.taskTitle          = taskTitle;
        this.taskDescription    = taskDescription;
        this.status             = status;
        this.user               = user;
        this.sort               = sort;
    }

    @JsonProperty
    @ApiModelProperty(value = "Title of task", required = true)
    public String getTaskTitle() { return taskTitle; }

    @JsonProperty
    @ApiModelProperty(value = "Description of task")
    public String getTaskDescription() { return taskDescription; }

    @JsonProperty
    @ApiModelProperty(value = "Order of task")
    public Integer getSort() { return sort; }

    @JsonProperty
    @ApiModelProperty(value = "Status of task. 0 - Inactive , 1 - Active", required = true, allowableValues = "0,1")
    public Integer status() { return status; }

    public void setUser(Users user) {
        this.user = user;
    }

    @JsonIgnore
    public Users getUser() { return user; }
}
