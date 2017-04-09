package com.huzzl.db;

import io.dropwizard.hibernate.AbstractDAO;
import com.huzzl.core.Users;
import org.hibernate.SessionFactory;

public class UsersDAO extends AbstractDAO<Users> {

    public UsersDAO(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    public Users create(Users user) {
        return persist(user);
    }

    public Users findUserByEmailAddress(String email) {
        Users u = uniqueResult(currentSession().getNamedQuery("Users.findByEmail").setParameter("email", email));
        return (u == null ? null : u);
    }
}
