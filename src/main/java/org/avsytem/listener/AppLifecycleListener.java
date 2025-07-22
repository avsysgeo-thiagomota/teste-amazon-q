package org.avsytem.listener;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Esta classe gerencia o ciclo de vida da aplicação web.
 * É responsável por inicializar recursos compartilhados, como o pool de conexões (DataSource),
 * quando a aplicação inicia, e liberá-los quando a aplicação para.
 */
@WebListener // 1. Anotação que registra o Listener no servidor
public class AppLifecycleListener implements ServletContextListener {

    private static final Logger LOGGER = Logger.getLogger(AppLifecycleListener.class.getName());

    /**
     * Este método é chamado pelo servidor EXATAMENTE UMA VEZ, quando a aplicação está iniciando.
     */
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        LOGGER.info("Iniciando aplicação: Configurando recursos compartilhados.");

        try {
            // 2. Faz o JNDI Lookup do DataSource (apenas uma vez!)
            Context initContext = new InitialContext();
            Context envContext = (Context) initContext.lookup("java:/comp/env");
            DataSource dataSource = (DataSource) envContext.lookup("jdbc/PostgresDB");

            // 3. Armazena a instância do DataSource no ServletContext
            // O ServletContext é um "mapa" global, compartilhado por toda a aplicação.
            ServletContext servletContext = sce.getServletContext();
            servletContext.setAttribute("dataSource", dataSource);

            LOGGER.info("DataSource configurado e armazenado no ServletContext com sucesso.");

        } catch (NamingException e) {
            LOGGER.log(Level.SEVERE, "ERRO CRÍTICO: Não foi possível inicializar o DataSource. A aplicação não funcionará corretamente.", e);
            // Em um cenário real, isso deveria impedir a aplicação de iniciar.
            throw new RuntimeException(e);
        }
    }

    /**
     * Este método é chamado pelo servidor EXATAMENTE UMA VEZ, quando a aplicação está parando.
     * Útil para fechar recursos, se necessário.
     */
    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        LOGGER.info("Finalizando aplicação: Liberando recursos.");
        // Se o DataSource precisasse ser fechado manualmente (raro em pools gerenciados),
        // o código viria aqui.
    }
}