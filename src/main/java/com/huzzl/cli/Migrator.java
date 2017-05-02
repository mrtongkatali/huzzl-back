package com.huzzl.cli;

import org.flywaydb.core.Flyway;

public class Migrator {

    public static void main(String[] args) throws Exception {

        System.out.println("\n\n\n database url: " + "jdbc:postgresql://" + System.getenv("JDBC_DATABASE_URL"));
        System.out.println("ENVI: " + System.getenv("ENVI"));

        if(System.getenv("ENVI") == "prod") {
            Flyway flyway = new Flyway();
            flyway.setDataSource("jdbc:postgresql://" + System.getenv("JDBC_DATABASE_URL"),
                    System.getenv("JDBC_DATABASE_USERNAME"),
                    System.getenv("JDBC_DATABASE_PASSWORD"));
            flyway.migrate();
        }

    }
}
