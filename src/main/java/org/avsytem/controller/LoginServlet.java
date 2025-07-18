package org.avsytem.controller;

import java.io.IOException;
import java.sql.Connection;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;

import com.google.gson.Gson;
import org.avsytem.dao.UserDAO;
import org.avsytem.database.PostgresConnection;
import org.mindrot.jbcrypt.BCrypt;

public class LoginServlet extends HttpServlet
{
    private static final long serialVersionUID = 1L;
    private UserDAO userDAO;

    @Override
    public void init() throws ServletException {
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
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        response.setContentType("application/json; charset=ISO-8859-1");
        String username = request.getParameter("usuario");
        String password = request.getParameter("senha");

        try
        {
            String storedHash = userDAO.getPasswordHashByUsername(username);

            if(storedHash == null) {
                response.getWriter().write("{\"success\": false, \"message\": \"Usuário ou senha inválidos.\"}");
            }
            else {
                if (BCrypt.checkpw(password, storedHash))
                {
                    int userId = userDAO.getIdByUsername(username);
                    HttpSession session = request.getSession();
                    session.setAttribute("username", username);
                    session.setAttribute("usuario_id", userId);

                    response.getWriter().write("{\"success\": true}");
                }
                else {
                    // Falha na autenticação
                    response.getWriter().write("{\"success\": false, \"message\": \"Usuário ou senha inválidos.\"}");
                }
            }

        }
        catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"success\": false, \"message\": \"Erro interno no servidor.\"}");
        }
    }
}
