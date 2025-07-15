package org.avsytem.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDAO {

    private final Connection connection;

    public UserDAO(Connection connection) {
        this.connection = connection;
    }

    /**
     * Busca o hash da senha de um usuário pelo seu username.
     * Retorna o hash se o usuário for encontrado e estiver ativo, caso contrário, null.
     */
    public String getPasswordHashByUsername(String username) throws SQLException {
        String sql = "SELECT password_hash FROM usuarios WHERE username = ? AND ativo = true";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, username);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("password_hash");
                }
            }
        }
        return null; // Usuário não encontrado ou inativo
    }
}