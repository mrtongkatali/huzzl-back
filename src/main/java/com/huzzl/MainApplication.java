package com.huzzl;

import com.github.toastshaman.dropwizard.auth.jwt.JwtAuthFilter;
import com.huzzl.auth.JwtAuthenticator;
import com.huzzl.auth.JwtAuthorizer;
import com.huzzl.core.AuthUser;
import com.huzzl.core.Task;
import com.huzzl.core.UserLogin;
import com.huzzl.core.Users;
import com.huzzl.db.TaskDAO;
import com.huzzl.db.UserLoginDAO;
import com.huzzl.db.UsersDAO;
import com.huzzl.resources.AuthResource;
import com.huzzl.resources.HelloResource;
import com.huzzl.resources.TaskResource;
import com.huzzl.service.AuthService;
import com.huzzl.service.TaskService;
import io.dropwizard.Application;
import io.dropwizard.auth.AuthDynamicFeature;
import io.dropwizard.auth.AuthValueFactoryProvider;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.flyway.FlywayBundle;
import io.dropwizard.flyway.FlywayFactory;
import io.dropwizard.hibernate.HibernateBundle;

import org.glassfish.jersey.server.filter.RolesAllowedDynamicFeature;
import org.hibernate.SessionFactory;
import org.jose4j.jwt.consumer.JwtConsumer;
import org.jose4j.jwt.consumer.JwtConsumerBuilder;
import org.jose4j.keys.HmacKey;

import java.security.Principal;
import java.util.Optional;


public class MainApplication extends Application<MainConfiguration> {

    // Instance
    private HibernateBundle<MainConfiguration> hibernateBundle;
    private SessionFactory sessionFactory;
    private TaskDAO taskDao;
    private UsersDAO usersDao;
    private UserLoginDAO userLoginDao;


    // Services
    private AuthService authService;
    private TaskService taskService;

    @Override
    public String getName() {
        return "huzzl";
    }

    public static HibernateBundle<MainConfiguration> createHibernateBundle() {
        return new HibernateBundle<MainConfiguration>(
                Task.class,
                Users.class,
                UserLogin.class
        ) {

            @Override
            public DataSourceFactory getDataSourceFactory(MainConfiguration config) {
                return config.getDataSourceFactory();
            }

        };
    }

    public static void main(String[] args) throws Exception {
        new MainApplication().run(args);
    }

    @Override
    public void initialize(Bootstrap<MainConfiguration> bootstrap) {
        hibernateBundle = createHibernateBundle();

        bootstrap.addBundle(hibernateBundle);

        bootstrap.addBundle(new FlywayBundle<MainConfiguration>() {

            @Override
            public DataSourceFactory getDataSourceFactory(MainConfiguration configuration) {
                return configuration.getDataSourceFactory();
            }

            @Override
            public FlywayFactory getFlywayFactory(MainConfiguration configuration) {
                return configuration.getFlywayFactory();
            }
        });
    }

    @Override
    public void run(MainConfiguration config,
                    Environment env) throws Exception {

        final byte[] key = config.getJwtTokenSecret();

        final JwtConsumer consumer = new JwtConsumerBuilder()
                .setAllowedClockSkewInSeconds(30)
                .setRequireExpirationTime()
                .setRequireSubject()
                .setVerificationKey(new HmacKey(key))
                .setRelaxVerificationKeyValidation()
                .build();

        // Run migration on application startup
        config.getFlywayFactory().build(config.getDataSourceFactory().build(env.metrics(),"postgresql")).migrate();

        this.sessionFactory = hibernateBundle.getSessionFactory();

        // DAOs
        this.taskDao        = new TaskDAO(sessionFactory);
        this.usersDao       = new UsersDAO(sessionFactory);
        this.userLoginDao   = new UserLoginDAO(sessionFactory);

        // Services
        this.authService    = new AuthService(userLoginDao, usersDao, config.getJwtTokenSecret());
        this.taskService    = new TaskService(taskDao);

        env.jersey().register(new AuthDynamicFeature(
                new JwtAuthFilter.Builder<AuthUser>()
                    .setJwtConsumer(consumer)
                    .setRealm("realm")
                    .setPrefix("bearer")
                    .setAuthenticator(new JwtAuthenticator())
                    .setAuthorizer(new JwtAuthorizer())
                    .buildAuthFilter()
        ));
        env.jersey().register(new AuthValueFactoryProvider.Binder<>(Principal.class));
        env.jersey().register(RolesAllowedDynamicFeature.class);

        env.jersey().register(new HelloResource(taskDao));
        env.jersey().register(new AuthResource(authService));
        env.jersey().register(new TaskResource(authService, taskService));
    }

}
