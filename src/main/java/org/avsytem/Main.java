package org.avsytem;

import org.avsytem.dao.ReceitaDAO;
import org.avsytem.database.PostgresConnection;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.SQLException;

public class Main {
    public static void main(String[] args) {
//        try (Connection conn = new PostgresConnection().createConnection()) {
//            ReceitaDAO dao = new ReceitaDAO(conn);
//            dao.criarEstruturaBanco();
//            dao.popularDadosIniciais();
//        }
//        catch (SQLException e) {
//            e.printStackTrace();
//        }
        String salt = BCrypt.gensalt();
        String senha = BCrypt.hashpw("admin123", salt);
    }
}
