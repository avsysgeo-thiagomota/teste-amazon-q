package org.avsytem.controller;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.avsytem.dao.ReceitaDAO;
import org.avsytem.database.PostgresConnection;
import org.avsytem.model.Receita;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class ReceitaServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private Gson gson;

    @Override
    public void init() throws ServletException {
        gson = new GsonBuilder().setPrettyPrinting().create();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        
        response.setContentType("application/json; charset=ISO-8859-1");

        try (Connection conn = new PostgresConnection().createConnection()) {
            ReceitaDAO dao = new ReceitaDAO(conn);

            if ("listar".equals(action)) {
                List<Receita> receitas = dao.listar();
                String jsonResponse = String.format("{\"total\": %d, \"receitas\": %s}",
                        receitas.size(), gson.toJson(receitas));
                response.getWriter().write(jsonResponse);
            } else {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("{\"success\": false, \"message\": \"Ação inválida ou não especificada.\"}");
            }
        } catch (SQLException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"success\": false, \"message\": \"Erro no banco de dados.\"}");
            e.printStackTrace();
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        String action = request.getParameter("action");
        response.setContentType("application/json; charset=ISO-8859-1");
        try (Connection conn = new PostgresConnection().createConnection()) {
            ReceitaDAO dao = new ReceitaDAO(conn);
            String jsonPayload = request.getParameter("jsonData");

            if (jsonPayload != null) {
                Receita receita = gson.fromJson(jsonPayload, Receita.class);
                if (receita.getId() == 0) {
                    dao.adicionar(receita);
                } else {
                    dao.atualizar(receita);
                }
                response.getWriter().write("{\"success\": true, \"message\": \"Receita salva com sucesso!\"}");

            } else if ("deletar".equals(action)) {
                int receitaId = Integer.parseInt(request.getParameter("id"));
                if (dao.deletar(receitaId)) {
                    response.getWriter().write("{\"success\": true}");
                } else {
                    response.getWriter().write("{\"success\": false, \"message\": \"Erro ao deletar receita.\"}");
                }
            } else {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("{\"success\": false, \"message\": \"Operação POST inválida.\"}");
            }
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"success\": false, \"message\": \"Erro interno do servidor.\"}");
            e.printStackTrace();
        }
    }
}