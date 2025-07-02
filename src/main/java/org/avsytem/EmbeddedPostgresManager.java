package org.avsytem;

import com.opentable.db.postgres.embedded.EmbeddedPostgres;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Utility class that manages a local embedded PostgreSQL instance.
 */
public class EmbeddedPostgresManager {
    private EmbeddedPostgres postgres;
    private Connection connection;

    /**
     * Starts the embedded PostgreSQL using the given directory. The directory is
     * created when it does not exist.
     */
    public void start(String directory) throws IOException, SQLException {
        if (postgres != null) {
            return;
        }
        File dir = new File(directory);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        postgres = EmbeddedPostgres.builder()
                .setDataDirectory(dir.toPath())
                .setCleanDataDirectory(false)
                .start();

        String url = postgres.getJdbcUrl("postgres", "postgres");
        connection = DriverManager.getConnection(url);
    }

    /**
     * Returns a connection to the embedded database.
     */
    public Connection getConnection() {
        return connection;
    }

    /**
     * Stops the embedded PostgreSQL and closes the connection.
     */
    public void stop() throws IOException, SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
        if (postgres != null) {
            postgres.close();
            postgres = null;
        }
    }
}
