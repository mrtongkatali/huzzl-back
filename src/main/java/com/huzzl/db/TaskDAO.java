package com.huzzl.db;

import io.dropwizard.hibernate.AbstractDAO;
import com.huzzl.core.Task;
import org.hibernate.SessionFactory;

import java.util.List;

public class TaskDAO extends AbstractDAO<Task> {

    public TaskDAO(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    public Task create(Task task) {
        return persist(task);
    }

    public List<Task> getAllTask() {
        return list(namedQuery("Task.allTask"));
    }

    public Task findTaskById(long id) {
        //return uniqueResult(currentSession().getNamedQuery("Task.findById").setParameter("id", id));
        //return (Task) currentSession().getNamedQuery("Task.findById").setParameter("id", id);

        Task task = uniqueResult(currentSession().getNamedQuery("Task.findById").setParameter("id", id));

        if (task == null) {
            task = null;
        }

        return task;
    }
}
