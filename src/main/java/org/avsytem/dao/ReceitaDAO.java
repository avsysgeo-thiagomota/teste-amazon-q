package org.avsytem.dao;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.avsytem.model.Ingrediente;
import org.avsytem.model.Passo;
import org.avsytem.model.Receita;

/**
 * DAO (Data Access Object) para a entidade Receita.
 * Lida com todas as operações de banco de dados para receitas,
 * incluindo a sincronização de seus detalhes (ingredientes e passos).
 */
public class ReceitaDAO {

    private static final Logger LOGGER = Logger.getLogger(ReceitaDAO.class.getName());

    // Definição da estrutura do banco (mantendo seus nomes de coluna originais)
    private static final String CREATE_TABLE_RECEITAS = "CREATE TABLE IF NOT EXISTS receitas (id SERIAL PRIMARY KEY, nome VARCHAR(255) NOT NULL, descricao TEXT, tempo_preparo_min INT, porcoes INT, dificuldade VARCHAR(50));";
    private static final String CREATE_TABLE_INGREDIENTES = "CREATE TABLE IF NOT EXISTS ingredientes (id SERIAL PRIMARY KEY, receita_id INT REFERENCES receitas(id) ON DELETE CASCADE, nome VARCHAR(255) NOT NULL, quantidade DOUBLE PRECISION, unidade VARCHAR(50));";
    private static final String CREATE_TABLE_PASSOS = "CREATE TABLE IF NOT EXISTS passos (id SERIAL PRIMARY KEY, receita_id INT REFERENCES receitas(id) ON DELETE CASCADE, ordem INT NOT NULL, descricao TEXT NOT NULL);";

    private Connection conn;

    public ReceitaDAO(Connection conn) {
        this.conn = conn;
    }

    public void criarEstruturaBanco() {
        try (Statement stmt = conn.createStatement()) {
            conn.setAutoCommit(false);
            stmt.execute(CREATE_TABLE_RECEITAS);
            stmt.execute(CREATE_TABLE_INGREDIENTES);
            stmt.execute(CREATE_TABLE_PASSOS);
            conn.commit();
            LOGGER.info("Estrutura do banco de dados verificada/criada com sucesso.");
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro ao criar estrutura do banco. Executando rollback.", e);
            try {
                conn.rollback();
            } catch (SQLException ex) {
                LOGGER.log(Level.SEVERE, "Erro crítico ao tentar executar rollback.", ex);
            }
        } finally {
            try {
                if (conn != null && !conn.isClosed()) {
                    conn.setAutoCommit(true);
                }
            } catch (SQLException e) {
                LOGGER.log(Level.SEVERE, "Erro ao restaurar auto-commit da conexão.", e);
            }
        }
    }

    public boolean popularDadosIniciais() {
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM receitas")) {
            if (rs.next() && rs.getInt(1) > 0) {
                LOGGER.info("O banco de dados já contém receitas. A população inicial não será executada.");
                return true;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "Não foi possível verificar a existência de receitas. Continuando com a população.", e);
        }

        try (InputStream inputStream = ReceitaDAO.class.getClassLoader().getResourceAsStream("receitas.json")) {
            if (inputStream == null) {
                LOGGER.severe("Arquivo 'receitas.json' não encontrado no classpath!");
                return false;
            }
            ObjectMapper mapper = new ObjectMapper();
            List<Receita> receitas = mapper.readValue(inputStream, new TypeReference<List<Receita>>() {});
            LOGGER.info("Populando o banco de dados com " + receitas.size() + " receitas do JSON.");
            for (Receita receita : receitas) {
                this.adicionar(receita);
            }
            LOGGER.info("População de dados iniciais concluída.");
            return true;
        } catch (IOException | SQLException e) {
            LOGGER.log(Level.SEVERE, "Falha ao popular o banco de dados com dados iniciais.", e);
            return false;
        }
    }

    public void adicionar(Receita receita) throws SQLException {
        
        String sqlInsertReceita = "INSERT INTO receitas (nome, descricao, tempo_preparo_min, porcoes, dificuldade) VALUES (?, ?, ?, ?, ?)";

        try {
            conn.setAutoCommit(false);
            try (PreparedStatement psReceita = conn.prepareStatement(sqlInsertReceita, Statement.RETURN_GENERATED_KEYS)) {
                psReceita.setString(1, receita.getNome());
                psReceita.setString(2, receita.getDescricao());
                
                psReceita.setInt(3, receita.getTempoDePreparo()); // O model usa getTempoDePreparo()
                psReceita.setInt(4, receita.getPorcoes());
                psReceita.setString(5, receita.getDificuldade());
                psReceita.executeUpdate();

                try (ResultSet generatedKeys = psReceita.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        receita.setId(generatedKeys.getInt(1));
                    } else {
                        throw new SQLException("Falha ao criar receita, nenhum ID obtido.");
                    }
                }
            }
            inserirIngredientes(receita);
            inserirPassos(receita);
            conn.commit();
        } catch (SQLException e) {
            conn.rollback();
            throw e;
        } finally {
            conn.setAutoCommit(true);
        }
    }

    public void atualizar(Receita receita) throws SQLException {
        
        String sqlUpdateReceita = "UPDATE receitas SET nome = ?, descricao = ?, tempo_preparo_min = ?, porcoes = ?, dificuldade = ? WHERE id = ?";

        try {
            conn.setAutoCommit(false);
            try (PreparedStatement psReceita = conn.prepareStatement(sqlUpdateReceita)) {
                psReceita.setString(1, receita.getNome());
                psReceita.setString(2, receita.getDescricao());
                
                psReceita.setInt(3, receita.getTempoDePreparo());
                psReceita.setInt(4, receita.getPorcoes());
                psReceita.setString(5, receita.getDificuldade());
                psReceita.setInt(6, receita.getId());
                psReceita.executeUpdate();
            }

            deletarIngredientesPorReceitaId(receita.getId());
            deletarPassosPorReceitaId(receita.getId());
            inserirIngredientes(receita);
            inserirPassos(receita);
            conn.commit();
        } catch (SQLException e) {
            conn.rollback();
            throw e;
        } finally {
            conn.setAutoCommit(true);
        }
    }

    public List<Receita> listar() throws SQLException {
        List<Receita> receitas = new ArrayList<>();
        String sql = "SELECT * FROM receitas ORDER BY nome ASC";

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Receita receita = new Receita();
                receita.setId(rs.getInt("id"));
                receita.setNome(rs.getString("nome"));
                receita.setDescricao(rs.getString("descricao"));
                
                receita.setTempoDePreparo(rs.getInt("tempo_preparo_min"));
                receita.setPorcoes(rs.getInt("porcoes"));
                receita.setDificuldade(rs.getString("dificuldade"));

                receita.setIngredientes(buscarIngredientesPorReceitaId(receita.getId()));
                receita.setPassos(buscarPassosPorReceitaId(receita.getId()));

                receitas.add(receita);
            }
        }
        return receitas;
    }

    public boolean deletar(int receitaId) throws SQLException {
        String sql = "DELETE FROM receitas WHERE id = ?";
        try {
            conn.setAutoCommit(false);
            deletarIngredientesPorReceitaId(receitaId);
            deletarPassosPorReceitaId(receitaId);

            try(PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, receitaId);
                int affectedRows = ps.executeUpdate();
                conn.commit();
                return affectedRows > 0;
            }
        } catch (SQLException e) {
            conn.rollback();
            throw e;
        } finally {
            conn.setAutoCommit(true);
        }
    }

    // ===================================================================
    // MÉTODOS AUXILIARES (Sem alterações necessárias aqui)
    // ===================================================================

    private void inserirIngredientes(Receita receita) throws SQLException {
        if (receita.getIngredientes() == null || receita.getIngredientes().isEmpty()) {
            return;
        }
        String sql = "INSERT INTO ingredientes (nome, quantidade, unidade, receita_id) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            for (Ingrediente ingrediente : receita.getIngredientes()) {
                ps.setString(1, ingrediente.getNome());
                ps.setDouble(2, ingrediente.getQuantidade());
                ps.setString(3, ingrediente.getUnidade());
                ps.setInt(4, receita.getId());
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }

    private void inserirPassos(Receita receita) throws SQLException {
        if (receita.getPassos() == null || receita.getPassos().isEmpty()) {
            return;
        }
        String sql = "INSERT INTO passos (ordem, descricao, receita_id) VALUES (?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            for (Passo passo : receita.getPassos()) {
                ps.setInt(1, passo.getOrdem());
                ps.setString(2, passo.getDescricao());
                ps.setInt(3, receita.getId());
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }

    private List<Ingrediente> buscarIngredientesPorReceitaId(int receitaId) throws SQLException {
        List<Ingrediente> ingredientes = new ArrayList<>();
        String sql = "SELECT * FROM ingredientes WHERE receita_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, receitaId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Ingrediente ingrediente = new Ingrediente();
                    ingrediente.setNome(rs.getString("nome"));
                    ingrediente.setQuantidade(rs.getDouble("quantidade"));
                    ingrediente.setUnidade(rs.getString("unidade"));
                    ingredientes.add(ingrediente);
                }
            }
        }
        return ingredientes;
    }

    private List<Passo> buscarPassosPorReceitaId(int receitaId) throws SQLException {
        List<Passo> passos = new ArrayList<>();
        String sql = "SELECT * FROM passos WHERE receita_id = ? ORDER BY ordem ASC";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, receitaId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Passo passo = new Passo();
                    passo.setOrdem(rs.getInt("ordem"));
                    passo.setDescricao(rs.getString("descricao"));
                    passos.add(passo);
                }
            }
        }
        return passos;
    }

    private void deletarIngredientesPorReceitaId(int receitaId) throws SQLException {
        String sql = "DELETE FROM ingredientes WHERE receita_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, receitaId);
            ps.executeUpdate();
        }
    }

    private void deletarPassosPorReceitaId(int receitaId) throws SQLException {
        String sql = "DELETE FROM passos WHERE receita_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, receitaId);
            ps.executeUpdate();
        }
    }
}