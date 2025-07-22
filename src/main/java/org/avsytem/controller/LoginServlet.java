package org.avsytem.controller;

import java.io.IOException;
import java.sql.Connection;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;

import com.google.gson.Gson;
import org.avsytem.dao.UserDAO;
import org.mindrot.jbcrypt.BCrypt;

public class LoginServlet extends HttpServlet
{
    private static final long serialVersionUID = 1L;
    private UserDAO userDAO;

    @Override
    public void init() throws ServletException
    {
        // 1. Pega o ServletContext (disponível em qualquer servlet)
        ServletContext servletContext = getServletContext();
        // 2. Pega o atributo "dataSource" que o nosso Listener armazenou
        DataSource dataSource = (DataSource) servletContext.getAttribute("dataSource");
        // Validação importante
        if (dataSource == null) {
            throw new ServletException("DataSource não encontrado no ServletContext. O AppLifecycleListener falhou ao iniciar?");
        }
        // 3. Cria o DAO com a instância compartilhada do DataSource
        this.userDAO = new UserDAO(dataSource);
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
