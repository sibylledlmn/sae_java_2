package fr.electronvert.facturation.servlet;

import fr.electronvert.facturation.servlet.util.FreeMarkerUtil;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

@WebListener
public class AppContextListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        FreeMarkerUtil.init(sce.getServletContext());
    }
}
