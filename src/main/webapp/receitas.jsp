<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="com.google.gson.Gson" %>
<%@ page import="java.util.Map" %>
<%@ page import="com.google.gson.reflect.TypeToken" %>
<%@ page import="org.avsytem.model.Receita" %>
<%@ page import="org.avsytem.model.Ingrediente" %>
<%@ page import="org.avsytem.model.Passo" %>
<%@ page contentType="application/json;charset=UTF-8" language="java" %>
<%!
    // --- SIMULAÇÃO DE UM BANCO DE DADOS ---
    // Em uma aplicação real, estes dados viriam de um banco de dados.
    private List<Receita> bancoDeReceitas = new ArrayList<>();
    private int proximoId = 1;
    private boolean dadosIniciados = false;
    private Gson gson = new Gson();

    // Método para inicializar os dados de exemplo apenas uma vez
    public void iniciarDados() {
        if (!dadosIniciados) {

            // Receita 1
            Receita r1 = new Receita();
            r1.setId(proximoId++);
            r1.setNome("Bolo de Chocolate da Vovó");
            r1.setDescricao("Um bolo fofinho e delicioso, receita de família.");
            r1.setTempoDePreparo(60);
            r1.setPorcoes(12);
            r1.setDificuldade("Fácil");
            r1.getIngredientes().add(new Ingrediente("Farinha de Trigo", 2, "xícaras"));
            r1.getIngredientes().add(new Ingrediente("Açúcar", 1.5, "xícaras"));
            r1.getPassos().add(new Passo(1, "Misture os ingredientes secos."));
            r1.getPassos().add(new Passo(2, "Adicione os líquidos e bata bem."));
            bancoDeReceitas.add(r1);

            // Receita 2
            Receita r2 = new Receita();
            r2.setId(proximoId++);
            r2.setNome("Frango Xadrez");
            r2.setDescricao("Prato oriental clássico, rápido e saboroso.");
            r2.setTempoDePreparo(40);
            r2.setPorcoes(4);
            r2.setDificuldade("Médio");
            bancoDeReceitas.add(r2);

            dadosIniciados = true;
        }
    }

    // Método para encontrar uma receita pelo ID
    public Receita findReceitaById(int id) {
        for (Receita r : bancoDeReceitas) {
            if (r.getId() == id) {
                return r;
            }
        }
        return null;
    }
%>
<%
    // Inicializa os dados de exemplo
    iniciarDados();

    String action = request.getParameter("action");
    String output = "";

    if ("listar".equals(action)) {
        // A PagingToolbar envia os parâmetros 'start' e 'limit'
        int start = request.getParameter("start") != null ? Integer.parseInt(request.getParameter("start")) : 0;
        int limit = request.getParameter("limit") != null ? Integer.parseInt(request.getParameter("limit")) : bancoDeReceitas.size();

        List<Receita> paginaDeReceitas = new ArrayList<>();
        for(int i = start; i < (start + limit) && i < bancoDeReceitas.size(); i++) {
            paginaDeReceitas.add(bancoDeReceitas.get(i));
        }

        // O JsonStore espera um objeto com 'total' e a lista de dados (aqui chamada de 'receitas')
        output = "{\"total\":" + bancoDeReceitas.size() + ",\"receitas\":" + gson.toJson(paginaDeReceitas) + "}";

    } else if ("deletar".equals(action)) {
        try {
            int id = Integer.parseInt(request.getParameter("id"));
            Receita receitaParaDeletar = findReceitaById(id);
            if (receitaParaDeletar != null) {
                bancoDeReceitas.remove(receitaParaDeletar);
                output = "{\"success\":true, \"message\":\"Receita deletada com sucesso.\"}";
            } else {
                output = "{\"success\":false, \"message\":\"Receita não encontrada.\"}";
            }
        } catch (Exception e) {
            output = "{\"success\":false, \"message\":\"ID inválido.\"}";
        }

    } else if ("salvar".equals(action)) {
        try {
            String idStr = request.getParameter("id");
            String nome = request.getParameter("nome");
            String descricao = request.getParameter("descricao");
            int tempo = Integer.parseInt(request.getParameter("tempoDePreparo"));
            int porcoes = Integer.parseInt(request.getParameter("porcoes"));
            String dificuldade = request.getParameter("dificuldade");

            // Decodifica as strings JSON de ingredientes e passos
            String ingredientesJson = request.getParameter("ingredientes");
            String passosJson = request.getParameter("passos");

            List<Ingrediente> ingredientes = gson.fromJson(ingredientesJson, new TypeToken<List<Ingrediente>>(){}.getType());
            List<Passo> passos = gson.fromJson(passosJson, new TypeToken<List<Passo>>(){}.getType());

            Receita receita;
            // Se o ID for vazio ou nulo, é uma nova receita
            if (idStr == null || idStr.isEmpty()) {
                receita = new Receita();
                receita.setId(proximoId++);
                bancoDeReceitas.add(receita);
            } else {
                // Senão, é uma edição
                receita = findReceitaById(Integer.parseInt(idStr));
            }

            // Atualiza os dados do objeto Receita
            receita.setNome(nome);
            receita.setDescricao(descricao);
            receita.setTempoDePreparo(tempo);
            receita.setPorcoes(porcoes);
            receita.setDificuldade(dificuldade);
            receita.setIngredientes(ingredientes);
            receita.setPassos(passos);

            // Resposta de sucesso para o FormPanel
            output = "{\"success\":true, \"message\":\"Receita salva com sucesso.\"}";

        } catch(Exception e) {
            output = "{\"success\":false, \"message\":\"Erro ao processar os dados da receita: " + e.getMessage() + "\"}";
        }
    }

    // Envia a resposta JSON para o cliente
    out.print(output);
    out.flush();
%>