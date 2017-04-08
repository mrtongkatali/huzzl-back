package com.huzzl.db;

import io.dropwizard.hibernate.AbstractDAO;
import com.huzzl.core.Users;
import org.hibernate.SessionFactory;

public class UsersDAO extends AbstractDAO<Users> {

    public UsersDAO(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

}
