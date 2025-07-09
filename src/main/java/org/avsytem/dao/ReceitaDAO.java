package org.avsytem.dao;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.avsytem.model.Ingrediente;
import org.avsytem.model.Passo;
import org.avsytem.model.Receita;

import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Objeto de Acesso a Dados (DAO) para a entidade Receita.
 * Gerencia todas as operações de CRUD (Criar, Ler, Atualizar, Deletar) para receitas no banco de dados.
 */
public class ReceitaDAO {

    private static final Logger LOGGER = Logger.getLogger(ReceitaDAO.class.getName());

    // --- Constantes SQL ---
    private static final String CREATE_TABLE_RECEITAS = "CREATE TABLE IF NOT EXISTS receitas (id SERIAL PRIMARY KEY, nome VARCHAR(255) NOT NULL, descricao TEXT, tempo_preparo_min INT, porcoes INT, dificuldade VARCHAR(50));";
    private static final String CREATE_TABLE_INGREDIENTES = "CREATE TABLE IF NOT EXISTS ingredientes (id SERIAL PRIMARY KEY, receita_id INT REFERENCES receitas(id) ON DELETE CASCADE, nome VARCHAR(255) NOT NULL, quantidade DOUBLE PRECISION, unidade VARCHAR(50));";
    private static final String CREATE_TABLE_PASSOS = "CREATE TABLE IF NOT EXISTS passos (id SERIAL PRIMARY KEY, receita_id INT REFERENCES receitas(id) ON DELETE CASCADE, ordem INT NOT NULL, descricao TEXT NOT NULL);";
    private static final String CREATE_TABLE_CATEGORIAS = "CREATE TABLE IF NOT EXISTS categorias (id SERIAL PRIMARY KEY, receita_id INT REFERENCES receitas(id) ON DELETE CASCADE, nome VARCHAR(100) NOT NULL);";

    private static final String INSERT_RECEITA = "INSERT INTO receitas(nome, descricao, tempo_preparo_min, porcoes, dificuldade) VALUES(?, ?, ?, ?, ?)";
    private static final String INSERT_INGREDIENTE = "INSERT INTO ingredientes(receita_id, nome, quantidade, unidade) VALUES(?, ?, ?, ?)";
    private static final String INSERT_PASSO = "INSERT INTO passos(receita_id, ordem, descricao) VALUES(?, ?, ?)";
    private static final String INSERT_CATEGORIA = "INSERT INTO categorias(receita_id, nome) VALUES(?, ?)";

    private static final String UPDATE_RECEITA = "UPDATE receitas SET nome = ?, descricao = ?, tempo_preparo_min = ?, porcoes = ?, dificuldade = ? WHERE id = ?";

    private static final String DELETE_INGREDIENTES_BY_RECEITA_ID = "DELETE FROM ingredientes WHERE receita_id = ?";
    private static final String DELETE_PASSOS_BY_RECEITA_ID = "DELETE FROM passos WHERE receita_id = ?";
    private static final String DELETE_CATEGORIAS_BY_RECEITA_ID = "DELETE FROM categorias WHERE receita_id = ?";
    private static final String DELETE_RECEITA_BY_ID = "DELETE FROM receitas WHERE id = ?";

    private static final String SELECT_RECEITAS_FULL =
            "SELECT r.*, " +
                    "i.id as ing_id, i.nome as ing_nome, i.quantidade as ing_qtd, i.unidade as ing_unidade, " +
                    "p.id as passo_id, p.ordem as passo_ordem, p.descricao as passo_desc, " +
                    "c.id as cat_id, c.nome as cat_nome " +
                    "FROM receitas r " +
                    "LEFT JOIN ingredientes i ON r.id = i.receita_id " +
                    "LEFT JOIN passos p ON r.id = p.receita_id " +
                    "LEFT JOIN categorias c ON r.id = c.receita_id ";

    private static final String SELECT_RECEITA_BY_ID_FULL = SELECT_RECEITAS_FULL + "WHERE r.id = ? ORDER BY p.ordem ASC";
    private static final String SELECT_ALL_RECEITAS_FULL = SELECT_RECEITAS_FULL + "ORDER BY r.id, p.ordem ASC";


    private final Connection connection;

    /**
     * Construtor que recebe uma conexão com o banco de dados.
     *
     * @param connection A conexão ativa com o banco de dados.
     */
    public ReceitaDAO(Connection connection) {
        this.connection = connection;
    }

    /**
     * Cria a estrutura de tabelas, se não existir.
     * Operação transacional para garantir a consistência.
     */
    public void criarEstruturaBanco() {
        // O try-with-resources garante que o Statement será fechado.
        try{
            Statement stmt = connection.createStatement();
            connection.setAutoCommit(false); // Inicia transação

            stmt.execute(CREATE_TABLE_RECEITAS);
            stmt.execute(CREATE_TABLE_INGREDIENTES);
            stmt.execute(CREATE_TABLE_PASSOS);
            stmt.execute(CREATE_TABLE_CATEGORIAS);

            connection.commit(); // Finaliza transação com sucesso
            LOGGER.info("Estrutura do banco de dados verificada/criada com sucesso.");

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro ao criar estrutura do banco. Executando rollback.", e);
            try {
                connection.rollback();
            } catch (SQLException ex) {
                LOGGER.log(Level.SEVERE, "Erro crítico ao tentar executar rollback.", ex);
            }
        } finally {
            try {
                if (connection != null && !connection.isClosed()) {
                    connection.setAutoCommit(true); // Restaura o modo padrão da conexão
                }
            } catch (SQLException e) {
                LOGGER.log(Level.SEVERE, "Erro ao restaurar auto-commit da conexão.", e);
            }
        }
    }

    /**
     * Adiciona uma receita completa (com detalhes) ao banco de dados.
     * Utiliza uma transação para garantir a atomicidade.
     */
    public void adicionar(Receita receita) throws SQLException {
        try {
            connection.setAutoCommit(false);

            // 1. Inserir a receita principal e obter o ID gerado
            try (PreparedStatement pstmt = connection.prepareStatement(INSERT_RECEITA, Statement.RETURN_GENERATED_KEYS)) {
                pstmt.setString(1, receita.getNome());
                pstmt.setString(2, receita.getDescricao());
                pstmt.setInt(3, receita.getTempoDePreparo());
                pstmt.setInt(4, receita.getPorcoes());
                pstmt.setString(5, receita.getDificuldade());
                pstmt.executeUpdate();

                try{
                    ResultSet rs = pstmt.getGeneratedKeys();
                    if (rs.next()) {
                        receita.setId(rs.getInt(1));
                    } else {
                        throw new SQLException("Falha ao obter ID da receita, nenhuma linha inserida.");
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }

            // 2. Inserir detalhes usando métodos auxiliares
            inserirIngredientes(receita.getId(), receita.getIngredientes());
            inserirPassos(receita.getId(), receita.getPassos());
            inserirCategorias(receita.getId(), receita.getCategorias());

            connection.commit();
        }
        catch (SQLException e)
        {
            LOGGER.log(Level.SEVERE, "Erro na transação de adição. Executando rollback.", e);
            connection.rollback();
            throw e;
        } finally {
            connection.setAutoCommit(true);
        }
    }

    /**
     * Atualiza uma receita existente.
     * A estratégia é atualizar a receita principal e recriar seus detalhes (ingredientes, passos, etc.).
     */
    public void atualizar(Receita receita) throws SQLException {
        try {
            connection.setAutoCommit(false);

            // 1. Atualizar a entidade principal 'receitas'
            try (PreparedStatement pstmt = connection.prepareStatement(UPDATE_RECEITA)) {
                pstmt.setString(1, receita.getNome());
                pstmt.setString(2, receita.getDescricao());
                pstmt.setInt(3, receita.getTempoDePreparo());
                pstmt.setInt(4, receita.getPorcoes());
                pstmt.setString(5, receita.getDificuldade());
                pstmt.setInt(6, receita.getId());
                pstmt.executeUpdate();
            }

            // 2. Apagar os detalhes antigos
            try (PreparedStatement pstmt = connection.prepareStatement(DELETE_INGREDIENTES_BY_RECEITA_ID)) {
                pstmt.setInt(1, receita.getId());
                pstmt.executeUpdate();
            }
            try (PreparedStatement pstmt = connection.prepareStatement(DELETE_PASSOS_BY_RECEITA_ID)) {
                pstmt.setInt(1, receita.getId());
                pstmt.executeUpdate();
            }
            try (PreparedStatement pstmt = connection.prepareStatement(DELETE_CATEGORIAS_BY_RECEITA_ID)) {
                pstmt.setInt(1, receita.getId());
                pstmt.executeUpdate();
            }

            // 3. Inserir os novos detalhes
            inserirIngredientes(receita.getId(), receita.getIngredientes());
            inserirPassos(receita.getId(), receita.getPassos());
            inserirCategorias(receita.getId(), receita.getCategorias());

            connection.commit();
        }
        catch (SQLException e)
        {
            LOGGER.log(Level.SEVERE, "Erro na transação de atualização. Executando rollback.", e);
            connection.rollback();
            throw e;
        }
        finally {
            connection.setAutoCommit(true);
        }
    }

    /**
     * Lista todas as receitas com seus detalhes usando uma única consulta otimizada.
     *
     * @return Uma lista de objetos Receita totalmente preenchidos.
     */
    public List<Receita> listar() throws SQLException {
        Map<Integer, Receita> receitasMap = new HashMap<>();
        try{
            PreparedStatement pstmt = connection.prepareStatement(SELECT_ALL_RECEITAS_FULL);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next())
            {
                int receitaId = rs.getInt("id");
                Receita receita = receitasMap.computeIfAbsent(receitaId, id ->
                {
                    try
                    {
                        return extrairReceitaBase(rs);
                    } catch (SQLException e) {
                        throw new RuntimeException(e); // Encapsula a exceção verificada
                    }
                });
                adicionarDetalhes(rs, receita);
            }
        }
        catch (RuntimeException e)
        {
            if (e.getCause() instanceof SQLException) {
                throw (SQLException) e.getCause();
            }
            throw e;
        }
        return new ArrayList<>(receitasMap.values());
    }

    /**
     * Busca uma única receita pelo ID com todos os seus detalhes.
     *
     * @return Um objeto Receita preenchido, ou null se não for encontrado.
     */
    public Receita buscarPorId(int id) throws SQLException
    {
        Map<Integer, Receita> receitasMap = new HashMap<>();
        try{
            PreparedStatement pstmt = connection.prepareStatement(SELECT_RECEITA_BY_ID_FULL);
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    int receitaId = rs.getInt("id");
                    Receita receita = receitasMap.computeIfAbsent(receitaId, rid -> {
                        try {
                            return extrairReceitaBase(rs);
                        } catch (SQLException e) {
                            throw new RuntimeException(e);
                        }
                    });
                    adicionarDetalhes(rs, receita);
                }
            }
        } catch (RuntimeException e) {
            if (e.getCause() instanceof SQLException) {
                throw (SQLException) e.getCause();
            }
            throw e;
        }
        return receitasMap.get(id);
    }

    /**
     * Deleta uma receita pelo ID. A remoção em cascata (ON DELETE CASCADE)
     * cuidará de deletar os ingredientes, passos e categorias associados.
     */
    public void deletar(int receitaId) throws SQLException {
        try
        {
            PreparedStatement pstmt = connection.prepareStatement(DELETE_RECEITA_BY_ID);
            pstmt.setInt(1, receitaId);
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                LOGGER.warning("Nenhuma receita encontrada com o ID: " + receitaId + " para deletar.");
            } else {
                LOGGER.info("Receita com ID: " + receitaId + " deletada com sucesso.");
            }
        }
        catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

    // --- Métodos Auxiliares Privados ---

    private void inserirIngredientes(int receitaId, List<Ingrediente> ingredientes) throws SQLException {
        if (ingredientes == null || ingredientes.isEmpty())
            return;

        try{
            PreparedStatement pstmt = connection.prepareStatement(INSERT_INGREDIENTE);
            for (Ingrediente ing : ingredientes) {
                pstmt.setInt(1, receitaId);
                pstmt.setString(2, ing.getNome());
                pstmt.setDouble(3, ing.getQuantidade());
                pstmt.setString(4, ing.getUnidade());
                pstmt.addBatch();
            }
            pstmt.executeBatch();
        }
        catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    private void inserirPassos(int receitaId, List<Passo> passos) throws SQLException {
        if (passos == null || passos.isEmpty()) return;
        try {
            PreparedStatement pstmt = connection.prepareStatement(INSERT_PASSO);
            for (Passo passo : passos) {
                pstmt.setInt(1, receitaId);
                pstmt.setInt(2, passo.getOrdem());
                pstmt.setString(3, passo.getDescricao());
                pstmt.addBatch();
            }
            pstmt.executeBatch();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void inserirCategorias(int receitaId, List<String> categorias) throws SQLException {
        if (categorias == null || categorias.isEmpty()) return;
        try{
            PreparedStatement pstmt = connection.prepareStatement(INSERT_CATEGORIA);
            for (String categoria : categorias) {
                pstmt.setInt(1, receitaId);
                pstmt.setString(2, categoria);
                pstmt.addBatch();
            }
            pstmt.executeBatch();
        }
        catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    private Receita extrairReceitaBase(ResultSet rs) throws SQLException {
        Receita receita = new Receita();
        receita.setId(rs.getInt("id"));
        receita.setNome(rs.getString("nome"));
        receita.setDescricao(rs.getString("descricao"));
        receita.setTempoDePreparo(rs.getInt("tempo_preparo_min"));
        receita.setPorcoes(rs.getInt("porcoes"));
        receita.setDificuldade(rs.getString("dificuldade"));
        return receita;
    }

    private void adicionarDetalhes(ResultSet rs, Receita receita) throws SQLException {
        // Adiciona ingrediente se existir e ainda não foi adicionado
        if (rs.getObject("ing_id") != null) {
            Ingrediente ing = new Ingrediente(rs.getString("ing_nome"), rs.getDouble("ing_qtd"), rs.getString("ing_unidade"));
            if (!receita.getIngredientes().contains(ing)) {
                receita.getIngredientes().add(ing);
            }
        }

        // Adiciona passo se existir e ainda não foi adicionado
        if (rs.getObject("passo_id") != null) {
            Passo passo = new Passo(rs.getInt("passo_ordem"), rs.getString("passo_desc"));
            if (!receita.getPassos().contains(passo)) {
                receita.getPassos().add(passo);
            }
        }

        // Adiciona categoria se existir e ainda não foi adicionada
        if (rs.getObject("cat_id") != null) {
            String categoria = rs.getString("cat_nome");
            if (!receita.getCategorias().contains(categoria)) {
                receita.getCategorias().add(categoria);
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
            Statement stmt = connection.createStatement();
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
}