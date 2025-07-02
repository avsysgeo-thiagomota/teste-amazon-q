package org.avsytem;

import java.sql.Connection;

/**
 * Simple example that starts an embedded PostgreSQL instance and
 * connects to it.
 */
public class Main {
    public static void main(String[] args) throws Exception {
        EmbeddedPostgresManager manager = new EmbeddedPostgresManager();
        try {
            manager.start("db-data");

            Connection connection = manager.getConnection();
            System.out.println("Conectado? " + !connection.isClosed());
        } finally {
            manager.stop();
        }
    }
}