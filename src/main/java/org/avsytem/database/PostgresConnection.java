package org.avsytem.database;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class PostgresConnection {
    private static final String DATABASE_CONFIGURATION_FILE = "database.properties";
    private final String url;
    private final String user;
    private final String password;

    public PostgresConnection() {

        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Driver do PostgreSQL não encontrado no classpath!", e);
        }

        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        Properties props = new Properties();

        try {
            InputStream resourceStream = loader.getResourceAsStream(DATABASE_CONFIGURATION_FILE);
            props.load(resourceStream);
        } catch (IOException e) {
            throw new RuntimeException("Erro ao ler config do PostgreSQL: " + e.getMessage(), e);
        }

        String host = props.getProperty("db.url");
        String port = props.getProperty("db.port");
        String banco = props.getProperty("db.banco");
        this.user = props.getProperty("db.user");
        this.password = props.getProperty("db.password");

        this.url = "jdbc:postgresql://" + host + ":" + port + "/" + banco;
    }

    public Connection createConnection() {
        try {
            return DriverManager.getConnection(url, user, password);
        } catch (SQLException e) {
            throw new RuntimeException(String.format("Erro ao conectar no PostgreSQL (%s, %s, %s)", url, user, password), e);
        }
    }
}
