package com.huzzl.db;

import com.huzzl.core.JwtAccessToken;
import io.dropwizard.hibernate.AbstractDAO;
import org.hibernate.SessionFactory;

/**
 * Created by mrtongkatali on 09/05/2017.
 */
public class JwtAccessTokenDAO extends AbstractDAO<JwtAccessToken> {

    public JwtAccessTokenDAO(SessionFactory sessionFactory) { super(sessionFactory); }

    public JwtAccessToken create(JwtAccessToken jwt) { return persist(jwt); }

    public JwtAccessToken findTaskById(long id) {
        return uniqueResult(currentSession().getNamedQuery("JWT.findById").setParameter("id", id));
    }

    public void update(JwtAccessToken jwt) {
        currentSession().merge(jwt);
    }
}
