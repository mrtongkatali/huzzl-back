package com.huzzl.db;

import com.huzzl.core.AuthUser;
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

    public List<Task> getAllTaskByUserId(Long user, Integer status, Integer count, Integer page) {
        return list(
                namedQuery("Task.getAllTaskByUserId")
                        .setParameter("user", user)
                        .setParameter("status", status)
                        .setFirstResult(page)
                        .setMaxResults(count)
        );
    }

    public Number countAllTaskByUserId(Long user, Integer status) {
        return (Number) currentSession().getNamedQuery("Task.countAllTaskByUserId")
                .setParameter("user", user)
                .setParameter("status", status).uniqueResult();
    }

    public Task findTaskById(long id) {
        return uniqueResult(currentSession().getNamedQuery("Task.findById").setParameter("id", id));
    }

    public void update(Task task) {
        currentSession().merge(task);
    }

}
