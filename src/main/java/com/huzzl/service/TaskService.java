package com.huzzl.service;

import com.huzzl.core.AuthUser;
import com.huzzl.core.Task;
import com.huzzl.db.TaskDAO;

import java.util.List;

public class TaskService {

    protected TaskDAO taskDao;

    public TaskService(TaskDAO taskDao) {
        this.taskDao = taskDao;
    }

    public Task createNewTask(Task task) {
        return taskDao.create(task);
    }

    public Task findTaskById(Long id) {
        Task task = taskDao.findTaskById(id);
        return (task == null ? null : task);
    }

    public List<Task> findAllTaskByUserId(Long user, Integer status, Integer count, Integer page) {
        return taskDao.getAllTaskByUserId(user, status, count, page);
    }

    public Number countAllTaskByUserId(Long user, Integer status) {
        return taskDao.countAllTaskByUserId(user, status);
    }


    public void update(Task task) {
        taskDao.update(task);
    }
}
