package com.huzzl.db;

import com.huzzl.core.UserLogin;
import io.dropwizard.hibernate.AbstractDAO;
import org.hibernate.SessionFactory;


public class UserLoginDAO  extends AbstractDAO<UserLogin> {

    public UserLoginDAO(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    public UserLogin create(UserLogin userLogin) { return persist(userLogin); }

    public UserLogin findUserByEmailAddress(String email) {
        return uniqueResult(currentSession().getNamedQuery("UsersLogin.findByEmailAddress").setParameter("email", email));
    }
}
