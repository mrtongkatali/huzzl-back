package com.huzzl;

import com.huzzl.core.Task;
import com.huzzl.db.TaskDAO;
import com.huzzl.resources.HelloResource;
import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.flyway.FlywayBundle;
import io.dropwizard.flyway.FlywayFactory;
import io.dropwizard.hibernate.HibernateBundle;

import org.hibernate.SessionFactory;


public class MainApplication extends Application<MainConfiguration> {

    // Instance
    private HibernateBundle<MainConfiguration> hibernateBundle;
    private SessionFactory sessionFactory;
    private TaskDAO taskDao;


    @Override
    public String getName() {
        return "huzzl";
    }

    public static HibernateBundle<MainConfiguration> createHibernateBundle() {
        return new HibernateBundle<MainConfiguration>(
                Task.class
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
                    Environment env) {

        // Run migration on application startup
        config.getFlywayFactory().build(config.getDataSourceFactory().build(env.metrics(),"postgresql")).migrate();

        this.sessionFactory = hibernateBundle.getSessionFactory();

        this.taskDao = new TaskDAO(sessionFactory);


        env.jersey().register(new HelloResource(taskDao));


    }

}
