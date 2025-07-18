package org.avsytem.controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.avsytem.dao.UserDAO;
import org.avsytem.database.PostgresConnection;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.BufferedReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.sql.DataSource;

public class UsuarioServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private Gson gson;
    private UserDAO userDAO;

    @Override
    public void init() throws ServletException {
        gson = new Gson();
        try {
            Context initContext = new InitialContext();
            Context envContext = (Context) initContext.lookup("java:/comp/env");
            DataSource dataSource = (DataSource) envContext.lookup("jdbc/PostgresDB");
            this.userDAO = new UserDAO(dataSource);

        } catch (NamingException e) {
            throw new ServletException("Erro crítico: Não foi possível encontrar o DataSource via JNDI.", e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json; charset=ISO-8859-1");
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = request.getReader())
        {
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        }

        JsonObject userData = gson.fromJson(sb.toString(), JsonObject.class);

        try 
        {
            userDAO.adicionar(userData.get("nome_completo").getAsString(), userData.get("email").getAsString(), userData.get("username").getAsString(), userData.get("senha").getAsString());

            response.getWriter().write("{\"success\": true, \"message\": \"Usuário cadastrado com sucesso!\"}");

        }
        catch (SQLException e)
        {
            String errorMessage = "Ocorreu um erro ao salvar no banco de dados.";

            // Código de erro para violação de constraint UNIQUE no PostgreSQL
            if (e.getSQLState().equals("23505"))
            {
                errorMessage = "O nome de usuário ou e-mail já está em uso.";
            }
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"success\": false, \"message\": \"" + errorMessage + "\"}");
            e.printStackTrace();
        }
        catch (Exception e)
        {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"success\": false, \"message\": \"Erro interno no servidor.\"}");
            e.printStackTrace();
        }
    }

    
    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("username") == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("{\"success\": false, \"message\": \"Sessão inválida ou expirada.\"}");
            return;
        }

        String username = (String) session.getAttribute("username");

        try  {
            
            if (userDAO.deletarPorUsername(username)) {
                session.invalidate(); // Invalida a sessão após deletar
                response.getWriter().write("{\"success\": true, \"message\": \"Conta excluída com sucesso.\"}");
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                response.getWriter().write("{\"success\": false, \"message\": \"Usuário não encontrado para exclusão.\"}");
            }
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"success\": false, \"message\": \"Erro interno ao excluir a conta.\"}");
            e.printStackTrace();
        }
    }
}
