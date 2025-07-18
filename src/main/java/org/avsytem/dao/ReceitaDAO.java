package org.avsytem.dao;

import org.avsytem.model.Ingrediente;
import org.avsytem.model.Passo;
import org.avsytem.model.Receita;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * DAO (Data Access Object) para a entidade Receita.
 * Lida com todas as operações de banco de dados para receitas de forma otimizada,
 * utilizando um DataSource para gerenciar um pool de conexões.
 */
public class ReceitaDAO {

    private static final Logger LOGGER = Logger.getLogger(ReceitaDAO.class.getName());

    // --- QUERIES SQL COMO CONSTANTES PARA MELHOR MANUTENÇÃO ---
    private static final String INSERT_RECEITA = "INSERT INTO receitas (nome, descricao, tempo_preparo_min, porcoes, dificuldade) VALUES (?, ?, ?, ?, ?)";
    private static final String INSERT_INGREDIENTE = "INSERT INTO ingredientes (receita_id, nome, quantidade, unidade) VALUES (?, ?, ?, ?)";
    private static final String INSERT_PASSO = "INSERT INTO passos (receita_id, ordem, descricao) VALUES (?, ?, ?)";

    private static final String UPDATE_RECEITA = "UPDATE receitas SET nome = ?, descricao = ?, tempo_preparo_min = ?, porcoes = ?, dificuldade = ? WHERE id = ?";

    private static final String DELETE_RECEITA = "DELETE FROM receitas WHERE id = ?";
    private static final String DELETE_INGREDIENTES_BY_RECEITA_ID = "DELETE FROM ingredientes WHERE receita_id = ?";
    private static final String DELETE_PASSOS_BY_RECEITA_ID = "DELETE FROM passos WHERE receita_id = ?";

    private static final String SELECT_ALL_RECEITAS_JOINED = "SELECT " +
            "r.id AS receita_id, r.nome AS receita_nome, r.descricao AS receita_descricao, " +
            "r.tempo_preparo_min, r.porcoes, r.dificuldade, " +
            "i.nome AS ingrediente_nome, i.quantidade, i.unidade, " +
            "p.ordem AS passo_ordem, p.descricao AS passo_descricao " +
            "FROM receitas r " +
            "LEFT JOIN ingredientes i ON r.id = i.receita_id " +
            "LEFT JOIN passos p ON r.id = p.receita_id " +
            "ORDER BY r.nome ASC, p.ordem ASC";


    private final DataSource dataSource;

    /**
     * Construtor que recebe o DataSource. A injeção do DataSource
     * é feita na camada que gerencia o ciclo de vida da aplicação (ex: no Servlet).
     * @param dataSource O pool de conexões a ser usado.
     */
    public ReceitaDAO(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    /**
     * Lista todas as receitas com seus ingredientes e passos em uma única e eficiente consulta.
     * Resolve o problema de N+1 queries.
     * @return Uma lista de todas as receitas.
     * @throws SQLException Se ocorrer um erro no banco de dados.
     */
    public List<Receita> listar() throws SQLException {
        Map<Integer, Receita> mapaDeReceitas = new LinkedHashMap<>();

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_ALL_RECEITAS_JOINED);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                int receitaId = rs.getInt("receita_id");
                Receita receitaAtual = mapaDeReceitas.get(receitaId);

                if (receitaAtual == null) {
                    receitaAtual = new Receita();
                    receitaAtual.setId(receitaId);
                    receitaAtual.setNome(rs.getString("receita_nome"));
                    receitaAtual.setDescricao(rs.getString("receita_descricao"));
                    receitaAtual.setTempoDePreparo(rs.getInt("tempo_preparo_min"));
                    receitaAtual.setPorcoes(rs.getInt("porcoes"));
                    receitaAtual.setDificuldade(rs.getString("dificuldade"));
                    mapaDeReceitas.put(receitaId, receitaAtual);
                }

                if (rs.getString("ingrediente_nome") != null) {
                    Ingrediente ingrediente = new Ingrediente(
                            rs.getString("ingrediente_nome"),
                            rs.getDouble("quantidade"),
                            rs.getString("unidade")
                    );
                    if (!receitaAtual.getIngredientes().contains(ingrediente)) {
                        receitaAtual.getIngredientes().add(ingrediente);
                    }
                }

                if (rs.getString("passo_descricao") != null) {
                    Passo passo = new Passo(
                            rs.getInt("passo_ordem"),
                            rs.getString("passo_descricao")
                    );
                    if (!receitaAtual.getPassos().contains(passo)) {
                        receitaAtual.getPassos().add(passo);
                    }
                }
            }
        }
        return new ArrayList<>(mapaDeReceitas.values());
    }

    /**
     * Adiciona uma nova receita e seus detalhes (ingredientes e passos) em uma única transação.
     * @param receita O objeto Receita a ser salvo.
     * @throws SQLException Se ocorrer um erro no banco de dados.
     */
    public void adicionar(Receita receita) throws SQLException {
        try (Connection conn = dataSource.getConnection()) {
            try {
                conn.setAutoCommit(false);

                try (PreparedStatement psReceita = conn.prepareStatement(INSERT_RECEITA, Statement.RETURN_GENERATED_KEYS)) {
                    psReceita.setString(1, receita.getNome());
                    psReceita.setString(2, receita.getDescricao());
                    psReceita.setInt(3, receita.getTempoDePreparo());
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

                inserirIngredientesEmLote(conn, receita.getId(), receita.getIngredientes());
                inserirPassosEmLote(conn, receita.getId(), receita.getPassos());

                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                LOGGER.log(Level.SEVERE, "Falha ao adicionar receita. Rollback executado.", e);
                throw e;
            }
        }
    }

    /**
     * Atualiza uma receita existente e seus detalhes.
     * A estratégia é deletar os detalhes antigos e inserir os novos em lote.
     * @param receita O objeto Receita com os dados atualizados.
     * @throws SQLException Se ocorrer um erro no banco de dados.
     */
    public void atualizar(Receita receita) throws SQLException {
        try (Connection conn = dataSource.getConnection()) {
            try {
                conn.setAutoCommit(false);

                try (PreparedStatement psReceita = conn.prepareStatement(UPDATE_RECEITA)) {
                    psReceita.setString(1, receita.getNome());
                    psReceita.setString(2, receita.getDescricao());
                    psReceita.setInt(3, receita.getTempoDePreparo());
                    psReceita.setInt(4, receita.getPorcoes());
                    psReceita.setString(5, receita.getDificuldade());
                    psReceita.setInt(6, receita.getId());
                    psReceita.executeUpdate();
                }

                deletarDetalhes(conn, receita.getId());
                inserirIngredientesEmLote(conn, receita.getId(), receita.getIngredientes());
                inserirPassosEmLote(conn, receita.getId(), receita.getPassos());

                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                LOGGER.log(Level.SEVERE, "Falha ao atualizar receita. Rollback executado.", e);
                throw e;
            }
        }
    }

    /**
     * Deleta uma receita do banco de dados usando seu ID.
     * Confia no 'ON DELETE CASCADE' configurado no banco para remover os detalhes.
     * @param receitaId O ID da receita a ser deletada.
     * @return true se a receita foi deletada, false caso contrário.
     * @throws SQLException Se ocorrer um erro no banco de dados.
     */
    public boolean deletar(int receitaId) throws SQLException {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(DELETE_RECEITA)) {
            ps.setInt(1, receitaId);
            int affectedRows = ps.executeUpdate();
            return affectedRows > 0;
        }
    }

    // --- MÉTODOS PRIVADOS AUXILIARES ---

    private void inserirIngredientesEmLote(Connection conn, int receitaId, List<Ingrediente> ingredientes) throws SQLException {
        if (ingredientes == null || ingredientes.isEmpty()) return;

        try (PreparedStatement ps = conn.prepareStatement(INSERT_INGREDIENTE)) {
            for (Ingrediente ingrediente : ingredientes) {
                ps.setInt(1, receitaId);
                ps.setString(2, ingrediente.getNome());
                ps.setDouble(3, ingrediente.getQuantidade());
                ps.setString(4, ingrediente.getUnidade());
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }

    private void inserirPassosEmLote(Connection conn, int receitaId, List<Passo> passos) throws SQLException {
        if (passos == null || passos.isEmpty()) return;

        try (PreparedStatement ps = conn.prepareStatement(INSERT_PASSO)) {
            for (Passo passo : passos) {
                ps.setInt(1, receitaId);
                ps.setInt(2, passo.getOrdem());
                ps.setString(3, passo.getDescricao());
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }

    private void deletarDetalhes(Connection conn, int receitaId) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(DELETE_INGREDIENTES_BY_RECEITA_ID)) {
            ps.setInt(1, receitaId);
            ps.executeUpdate();
        }
        try (PreparedStatement ps = conn.prepareStatement(DELETE_PASSOS_BY_RECEITA_ID)) {
            ps.setInt(1, receitaId);
            ps.executeUpdate();
        }
    }
}