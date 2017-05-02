package com.huzzl.cli;

import org.flywaydb.core.Flyway;

public class Migrator {

    public static void main(String[] args) throws Exception {

        System.out.println("\n\n\n database url: " + "jdbc:postgresql://" + System.getenv("JDBC_DATABASE_URL"));
        System.out.println("ENVI: " + System.getenv("ENVI"));

        String JDBC_DATABASE_URL        = ( System.getenv("JDBC_DATABASE_URL") == null ? "jdbc:postgresql://192.168.1.50/huzzldb" : System.getenv("JDBC_DATABASE_URL"));
        String JDBC_DATABASE_USERNAME   = ( System.getenv("JDBC_DATABASE_USERNAME") == null ? "devuser" : System.getenv("JDBC_DATABASE_USERNAME"));
        String JDBC_DATABASE_PASSWORD   = ( System.getenv("JDBC_DATABASE_PASSWORD") == null ? "demo123" : System.getenv("JDBC_DATABASE_PASSWORD"));

        Flyway flyway = new Flyway();

        flyway.setDataSource( JDBC_DATABASE_URL, JDBC_DATABASE_USERNAME, JDBC_DATABASE_PASSWORD);

        flyway.migrate();

    }
}
