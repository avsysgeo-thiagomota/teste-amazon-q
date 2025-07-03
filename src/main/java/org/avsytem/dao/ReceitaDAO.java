package org.avsytem.dao;

import org.avsytem.EmbeddedPostgresManager;
import org.avsytem.model.Ingrediente;
import org.avsytem.model.Passo;
import org.avsytem.model.Receita;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReceitaDAO {

    private final Connection connection;

    public ReceitaDAO(EmbeddedPostgresManager embeddedPostgresManager) throws SQLException {
        // O construtor recebe o manager e obtém uma conexão que será usada
        // por todos os métodos da instância do DAO.
        this.connection = embeddedPostgresManager.getConnection();
    }

    /**
     * Cria todas as tabelas necessárias para o sistema de receitas, se não existirem.
     * Usa transação para garantir que todas as tabelas sejam criadas ou nenhuma seja.
     * Este método deve ser chamado uma vez após a criação do DAO.
     */
    public void criarEstruturaBanco() {
        String sqlReceitas =
                "CREATE TABLE IF NOT EXISTS receitas (" +
                        "id SERIAL PRIMARY KEY," +
                        "nome VARCHAR(255) NOT NULL," +
                        "descricao TEXT," +
                        "tempo_preparo_min INT," +
                        "porcoes INT," +
                        "dificuldade VARCHAR(50)" +
                        ");";

        String sqlIngredientes =
                "CREATE TABLE IF NOT EXISTS ingredientes (" +
                        "id SERIAL PRIMARY KEY," +
                        "receita_id INT REFERENCES receitas(id) ON DELETE CASCADE," +
                        "nome VARCHAR(255) NOT NULL," +
                        "quantidade DOUBLE PRECISION," +
                        "unidade VARCHAR(50)" +
                        ");";

        String sqlPassos =
                "CREATE TABLE IF NOT EXISTS passos (" +
                        "id SERIAL PRIMARY KEY," +
                        "receita_id INT REFERENCES receitas(id) ON DELETE CASCADE," +
                        "ordem INT NOT NULL," +
                        "descricao TEXT NOT NULL" +
                        ");";

        String sqlCategorias =
                "CREATE TABLE IF NOT EXISTS categorias (" +
                        "id SERIAL PRIMARY KEY," +
                        "receita_id INT REFERENCES receitas(id) ON DELETE CASCADE," +
                        "nome VARCHAR(100) NOT NULL" +
                        ");";

        // Usando try-with-resources para garantir que o Statement seja fechado.
        try{
            Statement stmt = connection.createStatement();
            connection.setAutoCommit(false); // Inicia transação

            stmt.execute(sqlReceitas);
            stmt.execute(sqlIngredientes);
            stmt.execute(sqlPassos);
            stmt.execute(sqlCategorias);

            connection.commit(); // Finaliza transação com sucesso
            System.out.println("Estrutura do banco de dados verificada/criada com sucesso.");

        } catch (SQLException e) {
            System.err.println("Erro ao criar estrutura do banco de dados. Fazendo rollback...");
            try {
                connection.rollback();
            } catch (SQLException ex) {
                System.err.println("Erro ao tentar fazer rollback: " + ex.getMessage());
            }
            e.printStackTrace();
        } finally {
            try {
                connection.setAutoCommit(true); // Restaura o modo padrão da conexão
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Adiciona uma receita completa ao banco de dados, incluindo seus ingredientes e passos.
     * Utiliza uma transação para garantir a atomicidade da operação.
     */
    public void adicionar(Receita receita) throws SQLException {
        String sqlReceita = "INSERT INTO receitas(nome, descricao, tempo_preparo_min, porcoes, dificuldade) VALUES(?, ?, ?, ?, ?)";
        String sqlIngrediente = "INSERT INTO ingredientes(receita_id, nome, quantidade, unidade) VALUES(?, ?, ?, ?)";
        String sqlPasso = "INSERT INTO passos(receita_id, ordem, descricao) VALUES(?, ?, ?)";
        String sqlCategoria = "INSERT INTO categorias(receita_id, nome) VALUES(?, ?)";

        try {
            this.connection.setAutoCommit(false); // Inicia transação

            // 1. Inserir a receita principal e obter o ID gerado
            try {
                PreparedStatement pstmtReceita = connection.prepareStatement(sqlReceita, Statement.RETURN_GENERATED_KEYS);
                pstmtReceita.setString(1, receita.getNome());
                pstmtReceita.setString(2, receita.getDescricao());
                pstmtReceita.setInt(3, receita.getTempoDePreparo());
                pstmtReceita.setInt(4, receita.getPorcoes());
                pstmtReceita.setString(5, receita.getDificuldade());
                pstmtReceita.executeUpdate();

                try {
                    ResultSet rs = pstmtReceita.getGeneratedKeys();
                    if (rs.next())
                        receita.setId(rs.getInt(1));
                    else
                        throw new SQLException("Falha ao obter ID da receita, nenhuma linha inserida.");
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            } finally {

            }

            // 2. Inserir os ingredientes associados
            try{
                PreparedStatement pstmtIngrediente = connection.prepareStatement(sqlIngrediente);
                for (Ingrediente ing : receita.getIngredientes()) {
                    pstmtIngrediente.setInt(1, receita.getId());
                    pstmtIngrediente.setString(2, ing.getNome());
                    pstmtIngrediente.setDouble(3, ing.getQuantidade());
                    pstmtIngrediente.setString(4, ing.getUnidade());
                    pstmtIngrediente.addBatch();
                }
                pstmtIngrediente.executeBatch();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

            // 3. Inserir os passos associados
            try {
                PreparedStatement pstmtPasso = connection.prepareStatement(sqlPasso);
                for (Passo passo : receita.getPassos()) {
                    pstmtPasso.setInt(1, receita.getId());
                    pstmtPasso.setInt(2, passo.getOrdem());
                    pstmtPasso.setString(3, passo.getDescricao());
                    pstmtPasso.addBatch();
                }
                pstmtPasso.executeBatch();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

            // 4. Inserir as categorias associadas
            try {
                PreparedStatement pstmtCategoria = connection.prepareStatement(sqlCategoria);
                for (String categoria : receita.getCategorias()) {
                    pstmtCategoria.setInt(1, receita.getId());
                    pstmtCategoria.setString(2, categoria);
                    pstmtCategoria.addBatch();
                }
                pstmtCategoria.executeBatch();
            } finally {

            }

            connection.commit(); // Confirma a transação se tudo ocorreu bem

        } catch (SQLException e) {
            System.err.println("Erro na transação de adição. Executando rollback...");
            connection.rollback(); // Desfaz todas as operações da transação
            throw e; // Re-lança a exceção para a camada de serviço/controlador saber do erro
        } finally {
            // ? CORREÇÃO: O bloco finally que fechava a conexão foi removido.
            // A conexão deve permanecer aberta para ser usada por outras chamadas ao DAO.
            // Quem gerencia o fechamento final da conexão é a classe Main.
            connection.setAutoCommit(true); // Restaura o modo padrão da conexão
        }
    }

    /**
     * Lista todas as receitas do banco de dados com seus respectivos detalhes.
     * @return uma lista de objetos Receita, totalmente preenchidos.
     */
    public List<Receita> listar() throws SQLException {
        Map<Integer, Receita> receitasMap = new HashMap<>();
        String sqlReceitas = "SELECT * FROM receitas";
        String sqlIngredientes = "SELECT * FROM ingredientes";
        String sqlPassos = "SELECT * FROM passos ORDER BY ordem ASC";
        String sqlCategorias = "SELECT * FROM categorias";

        // 1. Buscar todas as receitas e popular o mapa inicial.
        try {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(sqlReceitas);
            while (rs.next()) {
                Receita receita = new Receita();
                int receitaId = rs.getInt("id");
                receita.setId(receitaId);
                receita.setNome(rs.getString("nome"));
                receita.setDescricao(rs.getString("descricao"));
                receita.setTempoDePreparo(rs.getInt("tempo_preparo_min"));
                receita.setPorcoes(rs.getInt("porcoes"));
                receita.setDificuldade(rs.getString("dificuldade"));
                receitasMap.put(receitaId, receita);
            }
        } finally {

        }

        // 2. Buscar todos os ingredientes e associá-los às receitas corretas.
        try {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(sqlIngredientes);
            while (rs.next()) {
                int receitaId = rs.getInt("receita_id");
                Receita receita = receitasMap.get(receitaId);
                if (receita != null) {
                    Ingrediente ingrediente = new Ingrediente(
                            rs.getString("nome"),
                            rs.getDouble("quantidade"),
                            rs.getString("unidade"));
                    receita.getIngredientes().add(ingrediente);
                }
            }
        } finally {

        }

        // 3. Buscar todos os passos e associá-los às receitas corretas.
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sqlPassos)) {
            while (rs.next()) {
                int receitaId = rs.getInt("receita_id");
                Receita receita = receitasMap.get(receitaId);
                if (receita != null) {
                    Passo passo = new Passo(
                            rs.getInt("ordem"),
                            rs.getString("descricao"));
                    receita.getPassos().add(passo);
                }
            }
        }

        // 4. Buscar todas as categorias e associá-las às receitas corretas.
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sqlCategorias)) {
            while (rs.next()) {
                int receitaId = rs.getInt("receita_id");
                Receita receita = receitasMap.get(receitaId);
                if (receita != null) {
                    receita.getCategorias().add(rs.getString("nome"));
                }
            }
        }

        return new ArrayList<>(receitasMap.values());
    }

    /**
     * Atualiza uma receita existente no banco de dados.
     * @param receita O objeto Receita com os dados atualizados. O ID da receita deve estar presente.
     * @throws SQLException se ocorrer um erro de banco de dados.
     */
    public void atualizar(Receita receita) throws SQLException {
        String sqlUpdateReceita = "UPDATE receitas SET nome = ?, descricao = ?, tempo_preparo_min = ?, porcoes = ?, dificuldade = ? WHERE id = ?";
        String sqlDeleteIngredientes = "DELETE FROM ingredientes WHERE receita_id = ?";
        String sqlDeletePassos = "DELETE FROM passos WHERE receita_id = ?";
        String sqlDeleteCategorias = "DELETE FROM categorias WHERE receita_id = ?";
        String sqlInsertIngrediente = "INSERT INTO ingredientes(receita_id, nome, quantidade, unidade) VALUES(?, ?, ?, ?)";
        String sqlInsertPasso = "INSERT INTO passos(receita_id, ordem, descricao) VALUES(?, ?, ?)";
        String sqlInsertCategoria = "INSERT INTO categorias(receita_id, nome) VALUES(?, ?)";

        try {
            connection.setAutoCommit(false);

            // 1. Atualizar a entidade principal 'receitas'
            try (PreparedStatement pstmt = connection.prepareStatement(sqlUpdateReceita)) {
                pstmt.setString(1, receita.getNome());
                pstmt.setString(2, receita.getDescricao());
                pstmt.setInt(3, receita.getTempoDePreparo());
                pstmt.setInt(4, receita.getPorcoes());
                pstmt.setString(5, receita.getDificuldade());
                pstmt.setInt(6, receita.getId());
                pstmt.executeUpdate();
            }

            // 2. Apagar os detalhes antigos
            try (PreparedStatement pstmtDel = connection.prepareStatement(sqlDeleteIngredientes)) {
                pstmtDel.setInt(1, receita.getId());
                pstmtDel.executeUpdate();
            }
            try (PreparedStatement pstmtDel = connection.prepareStatement(sqlDeletePassos)) {
                pstmtDel.setInt(1, receita.getId());
                pstmtDel.executeUpdate();
            }
            try (PreparedStatement pstmtDel = connection.prepareStatement(sqlDeleteCategorias)) {
                pstmtDel.setInt(1, receita.getId());
                pstmtDel.executeUpdate();
            }

            // 3. Inserir os novos detalhes (ingredientes, passos, categorias)
            // (Reutilizando a mesma lógica do método adicionar)
            try (PreparedStatement pstmt = connection.prepareStatement(sqlInsertIngrediente)) {
                for (Ingrediente ing : receita.getIngredientes()) {
                    pstmt.setInt(1, receita.getId());
                    pstmt.setString(2, ing.getNome());
                    pstmt.setDouble(3, ing.getQuantidade());
                    pstmt.setString(4, ing.getUnidade());
                    pstmt.addBatch();
                }
                pstmt.executeBatch();
            }
            try (PreparedStatement pstmt = connection.prepareStatement(sqlInsertPasso)) {
                for (Passo passo : receita.getPassos()) {
                    pstmt.setInt(1, receita.getId());
                    pstmt.setInt(2, passo.getOrdem());
                    pstmt.setString(3, passo.getDescricao());
                    pstmt.addBatch();
                }
                pstmt.executeBatch();
            }
            try (PreparedStatement pstmt = connection.prepareStatement(sqlInsertCategoria)) {
                for (String categoria : receita.getCategorias()) {
                    pstmt.setInt(1, receita.getId());
                    pstmt.setString(2, categoria);
                    pstmt.addBatch();
                }
                pstmt.executeBatch();
            }

            connection.commit();

        } catch (SQLException e) {
            System.err.println("Erro na transação de atualização. Executando rollback...");
            connection.rollback();
            throw e;
        } finally {
            connection.setAutoCommit(true);
        }
    }

    /**
     * Deleta uma receita do banco de dados usando seu ID.
     * @param receitaId O ID da receita a ser deletada.
     * @throws SQLException se ocorrer um erro de banco de dados.
     */
    public void deletar(int receitaId) throws SQLException {
        String sql = "DELETE FROM receitas WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, receitaId);
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                System.err.println("Nenhuma receita encontrada com o ID: " + receitaId + " para deletar.");
            } else {
                System.out.println("Receita com ID: " + receitaId + " deletada com sucesso.");
            }
        }
        // Nenhuma transação complexa aqui, então o try-with-resources é suficiente.
    }

    /**
     * Busca uma única receita pelo seu ID, preenchendo todos os seus detalhes.
     * @param id O ID da receita a ser buscada.
     * @return Um objeto Receita preenchido, ou null se não for encontrado.
     * @throws SQLException se ocorrer um erro de banco de dados.
     */
    public Receita buscarPorId(int id) throws SQLException {
        String sqlReceita = "SELECT * FROM receitas WHERE id = ?";
        String sqlIngredientes = "SELECT * FROM ingredientes WHERE receita_id = ?";
        String sqlPassos = "SELECT * FROM passos WHERE receita_id = ? ORDER BY ordem ASC";
        String sqlCategorias = "SELECT * FROM categorias WHERE receita_id = ?";

        Receita receita = null;

        // 1. Buscar a receita principal
        try (PreparedStatement pstmt = connection.prepareStatement(sqlReceita)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    receita = new Receita();
                    receita.setId(rs.getInt("id"));
                    receita.setNome(rs.getString("nome"));
                    receita.setDescricao(rs.getString("descricao"));
                    receita.setTempoDePreparo(rs.getInt("tempo_preparo_min"));
                    receita.setPorcoes(rs.getInt("porcoes"));
                    receita.setDificuldade(rs.getString("dificuldade"));
                } else {
                    return null; // Receita não encontrada
                }
            }
        }

        // Se a receita foi encontrada, busca os detalhes
        // 2. Buscar ingredientes
        try (PreparedStatement pstmt = connection.prepareStatement(sqlIngredientes)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    receita.getIngredientes().add(new Ingrediente(
                            rs.getString("nome"),
                            rs.getDouble("quantidade"),
                            rs.getString("unidade")
                    ));
                }
            }
        }

        // 3. Buscar passos
        try (PreparedStatement pstmt = connection.prepareStatement(sqlPassos)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    receita.getPassos().add(new Passo(
                            rs.getInt("ordem"),
                            rs.getString("descricao")
                    ));
                }
            }
        }

        // 4. Buscar categorias
        try (PreparedStatement pstmt = connection.prepareStatement(sqlCategorias)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    receita.getCategorias().add(rs.getString("nome"));
                }
            }
        }

        return receita;
    }
}