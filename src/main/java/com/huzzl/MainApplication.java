package com.huzzl;

import com.github.toastshaman.dropwizard.auth.jwt.JwtAuthFilter;
import com.huzzl.auth.JwtAuthenticator;
import com.huzzl.auth.JwtAuthorizer;
import com.huzzl.core.*;
import com.huzzl.db.JwtAccessTokenDAO;
import com.huzzl.db.TaskDAO;
import com.huzzl.db.UserLoginDAO;
import com.huzzl.db.UsersDAO;
import com.huzzl.resources.AuthResource;
import com.huzzl.resources.HelloResource;
import com.huzzl.resources.TaskResource;
import com.huzzl.service.AuthService;
import com.huzzl.service.TaskService;
import de.thomaskrille.dropwizard_template_config.TemplateConfigBundle;
import io.dropwizard.Application;
import io.dropwizard.auth.AuthDynamicFeature;
import io.dropwizard.auth.AuthValueFactoryProvider;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.flyway.FlywayBundle;
import io.dropwizard.flyway.FlywayFactory;
import io.dropwizard.hibernate.HibernateBundle;

import org.eclipse.jetty.servlets.CrossOriginFilter;
import org.glassfish.jersey.server.filter.RolesAllowedDynamicFeature;
import org.hibernate.SessionFactory;
import org.jose4j.jwt.consumer.JwtConsumer;
import org.jose4j.jwt.consumer.JwtConsumerBuilder;
import org.jose4j.keys.HmacKey;

import javax.servlet.DispatcherType;
import javax.servlet.FilterRegistration;
import java.security.Principal;
import java.util.EnumSet;

public class MainApplication extends Application<MainConfiguration> {

    // Instance
    private HibernateBundle<MainConfiguration> hibernateBundle;
    private SessionFactory sessionFactory;
    private TaskDAO taskDao;
    private UsersDAO usersDao;
    private UserLoginDAO userLoginDao;
    private JwtAccessTokenDAO jwtAccessTokenDao;


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
                UserLogin.class,
                JwtAccessToken.class
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

        bootstrap.addBundle(new TemplateConfigBundle());
    }

    @Override
    public void run(MainConfiguration config,
                    Environment env) throws Exception {

        // Configure JWT
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
        this.taskDao            = new TaskDAO(sessionFactory);
        this.usersDao           = new UsersDAO(sessionFactory);
        this.userLoginDao       = new UserLoginDAO(sessionFactory);
        this.jwtAccessTokenDao  = new JwtAccessTokenDAO(sessionFactory);

        // Services
        this.authService    = new AuthService(userLoginDao, usersDao, jwtAccessTokenDao, config.getJwtTokenSecret());
        this.taskService    = new TaskService(taskDao);

        // Enable CORS
        final FilterRegistration.Dynamic cors =
                env.servlets().addFilter("CORS", CrossOriginFilter.class);

        cors.setInitParameter("allowedOrigins", "*");
        cors.setInitParameter("allowedHeaders", "X-Requested-With,Content-Type,Accept,Origin");
        cors.setInitParameter("allowedMethods", "OPTIONS,GET,PUT,POST,DELETE,HEAD");

        // Add URL Mapping
        cors.addMappingForUrlPatterns(EnumSet.allOf(DispatcherType.class), true, "/*");

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

        env.jersey().register(new HelloResource());
        env.jersey().register(new AuthResource(authService));
        env.jersey().register(new TaskResource(authService, taskService));
    }

}
