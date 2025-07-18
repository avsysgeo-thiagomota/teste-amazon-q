package org.avsytem.dao;

import org.mindrot.jbcrypt.BCrypt;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Logger;

/**
 * DAO para a entidade Usuario.
 * Lida com operações de banco de dados para usuários de forma otimizada,
 * utilizando um DataSource para gerenciar um pool de conexões.
 */
public class UserDAO {

    private static final Logger LOGGER = Logger.getLogger(UserDAO.class.getName());

    // 1. As queries são definidas como constantes para clareza e manutenção.
    private static final String INSERT_USER_SQL = "INSERT INTO usuarios (nome_completo, email, username, password_hash) VALUES (?, ?, ?, ?)";
    private static final String GET_HASH_SQL = "SELECT password_hash FROM usuarios WHERE username = ? AND ativo = true";
    private static final String DELETE_USER_SQL = "DELETE FROM usuarios WHERE username = ?";

    // 2. O DAO armazena a referência ao pool de conexões.
    private final DataSource dataSource;

    /**
     * Construtor que recebe o DataSource.
     * @param dataSource O pool de conexões a ser usado.
     */
    public UserDAO(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    /**
     * Adiciona um novo usuário ao banco de dados, com a senha criptografada.
     * @param nomeCompleto O nome completo do usuário.
     * @param email O email do usuário.
     * @param username O nome de usuário para login.
     * @param plainTextPassword A senha em texto plano, que será criptografada.
     * @throws SQLException se ocorrer um erro no banco, como username duplicado.
     */
    public void adicionar(String nomeCompleto, String email, String username, String plainTextPassword) throws SQLException {
        String hashedPassword = BCrypt.hashpw(plainTextPassword, BCrypt.gensalt());

        // 3. O método obtém, usa e fecha sua própria conexão do pool.
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(INSERT_USER_SQL)) {

            pstmt.setString(1, nomeCompleto);
            pstmt.setString(2, email);
            pstmt.setString(3, username);
            pstmt.setString(4, hashedPassword);
            pstmt.executeUpdate();
        }
    }

    /**
     * Busca o hash da senha de um usuário pelo seu username.
     * Retorna o hash se o usuário for encontrado e estiver ativo, caso contrário, null.
     */
    public String getPasswordHashByUsername(String username) throws SQLException {
        // 3. O método obtém, usa e fecha sua própria conexão do pool.
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(GET_HASH_SQL)) {

            pstmt.setString(1, username);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("password_hash");
                }
            }
        }
        return null; // Usuário não encontrado ou inativo
    }

    /**
     * Deleta um usuário do banco de dados pelo seu username.
     * @param username O nome de usuário a ser deletado.
     * @return true se um usuário foi deletado, false caso contrário.
     * @throws SQLException se ocorrer um erro no banco.
     */
    public boolean deletarPorUsername(String username) throws SQLException {
        // 3. O método obtém, usa e fecha sua própria conexão do pool.
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(DELETE_USER_SQL)) {

            pstmt.setString(1, username);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        }
    }
}