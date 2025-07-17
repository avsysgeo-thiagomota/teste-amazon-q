package org.avsytem.controller;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class LogoutServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        response.setContentType("application/json; charset=ISO-8859-1");
        try {
            // Pega a sessão atual, sem criar uma nova se não existir
            HttpSession session = request.getSession(false);
            if (session != null) {
                // Invalida a sessão, removendo todos os atributos (como o 'username')
                session.invalidate();
            }
            // Retorna uma resposta de sucesso para o cliente
            response.getWriter().write("{\"success\": true}");
        }
        catch (Exception e)
        {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"success\": false, \"message\": \"Erro ao processar o logout.\"}");
            e.printStackTrace();
        }
    }
}
