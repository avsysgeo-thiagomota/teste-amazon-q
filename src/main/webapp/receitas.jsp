<%@ page import="org.avsytem.dao.ReceitaDAO" %>
<%@ page import="org.avsytem.dao.ReceitaDAO" %>
<%@ page import="org.avsytem.model.Receita" %>
<%@ page import="java.util.List" %>
<%@ page import="com.google.gson.Gson" %>
<%@ page import="com.google.gson.JsonObject" %>
<%@ page import="java.util.HashMap" %>
<%@ page import="java.util.Map" %>
<%@ page import="org.avsytem.database.PostgresConnection"%>
<%@ page contentType="application/json; charset=UTF-8" %>
<%
    PostgresConnection pgConn = new PostgresConnection();
    ReceitaDAO dao = new ReceitaDAO(pgConn.createConnection());
    dao.criarEstruturaBanco();
    dao.popularDadosIniciais();

    Gson gson = new Gson();
    String action = request.getParameter("action");

    try {
        if ("listar".equals(action)) {
            List<Receita> listaReceitas = dao.listar();
            Map<String, Object> responseData = new HashMap<>();
            responseData.put("total", listaReceitas.size());
            responseData.put("receitas", listaReceitas);
            out.print(gson.toJson(responseData));

        } else if ("deletar".equals(action)) {
            int id = Integer.parseInt(request.getParameter("id"));
            dao.deletar(id);
            out.print("{\"success\": true, \"message\": \"Receita deletada com sucesso.\"}");

        } else if ("salvar".equals(action)) {
            String idParam = request.getParameter("id");
            Receita receita;
            boolean isNew = idParam == null || idParam.isEmpty() || "0".equals(idParam);

            if (isNew) {
                receita = new Receita();
            } else {
                receita = dao.buscarPorId(Integer.parseInt(idParam));
                if (receita == null) {
                     response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                     out.print("{\"success\": false, \"message\": \"Receita não encontrada para atualização.\"}");
                     return;
                }
            }

            receita.setNome(request.getParameter("nome"));
            receita.setDescricao(request.getParameter("descricao"));
            receita.setTempoDePreparo(Integer.parseInt(request.getParameter("tempoDePreparo")));
            receita.setPorcoes(Integer.parseInt(request.getParameter("porcoes")));
            receita.setDificuldade(request.getParameter("dificuldade"));

            if (isNew) {
                dao.adicionar(receita);
            } else {
                dao.atualizar(receita);
            }

            JsonObject responseJson = new JsonObject();
            responseJson.addProperty("success", true);
            responseJson.add("data", gson.toJsonTree(receita));
            out.print(responseJson.toString());
        }
    } catch (Exception e) {
        e.printStackTrace();
        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        out.print("{\"success\": false, \"message\": \"" + e.getMessage().replace("\"", "'") + "\"}");
    }

    out.flush();
%>