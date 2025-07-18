package org.avsytem.controller;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.http.*;

import org.avsytem.dao.ReceitaDAO;
import org.avsytem.database.PostgresConnection;
import org.avsytem.model.Receita;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.sql.DataSource;

public class ReceitaServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private Gson gson;
    private ReceitaDAO dao;

    @Override
    public void init() throws ServletException {
        gson = new GsonBuilder().setPrettyPrinting().create();
        try {
            Context initContext = new InitialContext();
            Context envContext = (Context) initContext.lookup("java:/comp/env");
            DataSource dataSource = (DataSource) envContext.lookup("jdbc/PostgresDB");
            this.dao = new ReceitaDAO(dataSource);

        } catch (NamingException e) {
            throw new ServletException("Erro crítico: Não foi possível encontrar o DataSource via JNDI.", e);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String action = request.getParameter("action");
        response.setContentType("application/json; charset=ISO-8859-1");

        try
        {
            if ("listar".equals(action)) {
                HttpSession session = request.getSession(false);
                Integer usuarioId = (Integer) session.getAttribute("usuario_id");

                if (usuarioId == null) {
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.getWriter().write("{\"success\": false, \"message\": \"Sessão inválida ou expirada.\"}");
                    return;
                }

                List<Receita> receitas = dao.listar(usuarioId);
                String jsonResponse = String.format("{\"total\": %d, \"receitas\": %s}", receitas.size(), gson.toJson(receitas));
                response.getWriter().write(jsonResponse);
            }
            else
            {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("{\"success\": false, \"message\": \"Ação inválida ou não especificada.\"}");
            }
        }
        catch (SQLException e)
        {
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
        try
        {
            String jsonPayload = request.getParameter("jsonData");

            HttpSession session = request.getSession(false);
            if (session == null || session.getAttribute("usuario_id") == null) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("{\"success\": false, \"message\": \"Sessão inválida para salvar. Faça o login novamente.\"}");
                return;
            }
            Integer usuarioId = (Integer) session.getAttribute("usuario_id");

            if (jsonPayload != null) {
                Receita receita = gson.fromJson(jsonPayload, Receita.class);
                receita.setUsuario_id(usuarioId);

                if (usuarioId == null) {
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.getWriter().write("{\"success\": false, \"message\": \"Sessão inválida para salvar.\"}");
                    return;
                }

                if (receita.getId() == 0)
                    dao.adicionar(receita);
                else
                    dao.atualizar(receita);

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