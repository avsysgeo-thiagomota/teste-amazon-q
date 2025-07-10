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
    private static final String CREATE_TABLE_RECEITAS = "CREATE TABLE IF NOT EXISTS receitas (id SERIAL PRIMARY KEY, nome VARCHAR(255) NOT NULL, descricao TEXT, tempo_preparo_min INT, porcoes INT, dificuldade VARCHAR(50));";
    private static final String CREATE_TABLE_INGREDIENTES = "CREATE TABLE IF NOT EXISTS ingredientes (id SERIAL PRIMARY KEY, receita_id INT REFERENCES receitas(id) ON DELETE CASCADE, nome VARCHAR(255) NOT NULL, quantidade DOUBLE PRECISION, unidade VARCHAR(50));";
    private static final String CREATE_TABLE_PASSOS = "CREATE TABLE IF NOT EXISTS passos (id SERIAL PRIMARY KEY, receita_id INT REFERENCES receitas(id) ON DELETE CASCADE, ordem INT NOT NULL, descricao TEXT NOT NULL);";
    private static final String CREATE_TABLE_CATEGORIAS = "CREATE TABLE IF NOT EXISTS categorias (id SERIAL PRIMARY KEY, receita_id INT REFERENCES receitas(id) ON DELETE CASCADE, nome VARCHAR(100) NOT NULL);";

    private Connection conn;

    public ReceitaDAO(Connection conn) {
        this.conn = conn;
    }

    /**
     * Cria a estrutura de tabelas, se não existir.
     * Operação transacional para garantir a consistência.
     */
    public void criarEstruturaBanco() {
        // O try-with-resources garante que o Statement será fechado.
        try{
            Statement stmt = conn.createStatement();
            conn.setAutoCommit(false); // Inicia transação

            stmt.execute(CREATE_TABLE_RECEITAS);
            stmt.execute(CREATE_TABLE_INGREDIENTES);
            stmt.execute(CREATE_TABLE_PASSOS);
            stmt.execute(CREATE_TABLE_CATEGORIAS);

            conn.commit(); // Finaliza transação com sucesso
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
                    conn.setAutoCommit(true); // Restaura o modo padrão da conexão
                }
            } catch (SQLException e) {
                LOGGER.log(Level.SEVERE, "Erro ao restaurar auto-commit da conexão.", e);
            }
        }
    }

    /**
     * Método utilitário para popular o banco com dados iniciais de um JSON.
     * Idealmente, esta lógica estaria em uma classe separada de inicialização de banco.
     */
    public boolean popularDadosIniciais() {
        // Verifica se já existem receitas para não popular novamente
        try
        {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM receitas");
            if (rs.next() && rs.getInt(1) > 0) {
                LOGGER.info("O banco de dados já contém receitas. A população inicial não será executada.");
                return true;
            }
        }
        catch (SQLException e) {
            LOGGER.log(Level.WARNING, "Não foi possível verificar a existência de receitas. Continuando com a população.", e);
        }

        try{
            InputStream inputStream = ReceitaDAO.class.getClassLoader().getResourceAsStream("receitas.json");
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
        }
        catch (IOException | SQLException e)
        {
            LOGGER.log(Level.SEVERE, "Falha ao popular o banco de dados com dados iniciais.", e);
            return false;
        }
    }

    /**
     * Adiciona uma nova receita completa (com ingredientes e passos) ao banco de dados.
     * A operação é realizada dentro de uma única transação.
     *
     * @param receita O objeto Receita a ser adicionado.
     * @throws SQLException Se ocorrer um erro no banco de dados.
     */
    public void adicionar(Receita receita) throws SQLException {
        String sqlInsertReceita = "INSERT INTO receitas (nome, descricao, tempoDePreparo, porcoes, dificuldade) VALUES (?, ?, ?, ?, ?)";

        try {
            // Inicia a transação
            conn.setAutoCommit(false);

            // 1. Insere a receita principal e obtém o ID gerado
            try (PreparedStatement psReceita = conn.prepareStatement(sqlInsertReceita, Statement.RETURN_GENERATED_KEYS)) {
                psReceita.setString(1, receita.getNome());
                psReceita.setString(2, receita.getDescricao());
                psReceita.setInt(3, receita.getTempoDePreparo());
                psReceita.setInt(4, receita.getPorcoes());
                psReceita.setString(5, receita.getDificuldade());
                psReceita.executeUpdate();

                // Pega o ID que o banco gerou para a nova receita
                try (ResultSet generatedKeys = psReceita.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        receita.setId(generatedKeys.getInt(1));
                    } else {
                        throw new SQLException("Falha ao criar receita, nenhum ID obtido.");
                    }
                }
            }

            // 2. Insere os ingredientes e passos vinculando-os ao novo ID da receita
            inserirIngredientes(receita);
            inserirPassos(receita);

            // 3. Se tudo ocorreu bem, efetiva a transação
            conn.commit();

        } catch (SQLException e) {
            // Se qualquer erro ocorrer, reverte todas as operações
            conn.rollback();
            throw e; // Propaga a exceção para ser tratada pela Servlet
        } finally {
            // Garante que o autocommit seja reativado
            conn.setAutoCommit(true);
        }
    }

    /**
     * Atualiza uma receita existente e sincroniza suas listas de ingredientes e passos.
     * Utiliza a estratégia "apagar e reinserir" para os detalhes, dentro de uma transação.
     *
     * @param receita O objeto Receita com os dados atualizados.
     * @throws SQLException Se ocorrer um erro no banco de dados.
     */
    public void atualizar(Receita receita) throws SQLException {
        String sqlUpdateReceita = "UPDATE receitas SET nome = ?, descricao = ?, tempoDePreparo = ?, porcoes = ?, dificuldade = ? WHERE id = ?";

        try {
            // Inicia a transação
            conn.setAutoCommit(false);

            // 1. Atualiza a entidade principal (Receita)
            try (PreparedStatement psReceita = conn.prepareStatement(sqlUpdateReceita)) {
                psReceita.setString(1, receita.getNome());
                psReceita.setString(2, receita.getDescricao());
                psReceita.setInt(3, receita.getTempoDePreparo());
                psReceita.setInt(4, receita.getPorcoes());
                psReceita.setString(5, receita.getDificuldade());
                psReceita.setInt(6, receita.getId());
                psReceita.executeUpdate();
            }

            // 2. Apaga os detalhes antigos
            deletarIngredientesPorReceitaId(receita.getId());
            deletarPassosPorReceitaId(receita.getId());

            // 3. Insere os novos detalhes
            inserirIngredientes(receita);
            inserirPassos(receita);

            // 4. Efetiva a transação
            conn.commit();

        } catch (SQLException e) {
            conn.rollback();
            throw e;
        } finally {
            conn.setAutoCommit(true);
        }
    }

    /**
     * Lista todas as receitas do banco de dados, incluindo seus respectivos
     * ingredientes e passos.
     *
     * @return Uma lista de objetos Receita totalmente preenchidos.
     * @throws SQLException Se ocorrer um erro no banco de dados.
     */
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
                receita.setTempoDePreparo(rs.getInt("tempoDePreparo"));
                receita.setPorcoes(rs.getInt("porcoes"));
                receita.setDificuldade(rs.getString("dificuldade"));

                // Para cada receita, carrega seus detalhes
                receita.setIngredientes(buscarIngredientesPorReceitaId(receita.getId()));
                receita.setPassos(buscarPassosPorReceitaId(receita.getId()));

                receitas.add(receita);
            }
        }
        return receitas;
    }

    /**
     * Deleta uma receita e seus detalhes associados do banco de dados.
     *
     * @param receitaId O ID da receita a ser deletada.
     * @return true se a deleção foi bem-sucedida, false caso contrário.
     * @throws SQLException Se ocorrer um erro no banco de dados.
     */
    public boolean deletar(int receitaId) throws SQLException {
        String sql = "DELETE FROM receitas WHERE id = ?";
        try {
            conn.setAutoCommit(false);

            // Primeiro deleta os filhos para evitar violação de chave estrangeira
            deletarIngredientesPorReceitaId(receitaId);
            deletarPassosPorReceitaId(receitaId);

            // Depois deleta o pai
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
    // MÉTODOS AUXILIARES PRIVADOS
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