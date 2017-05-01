package com.huzzl.service;

import com.huzzl.core.Task;
import com.huzzl.db.TaskDAO;

public class TaskService {

    protected TaskDAO taskDao;

    public TaskService(TaskDAO taskDao) {
        this.taskDao = taskDao;
    }

    public Task createNewTask(Task task) {
        return taskDao.create(task);
    }

}
