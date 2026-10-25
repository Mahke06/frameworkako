package framework.listener;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import java.util.HashMap;

import framework.container.Conteneur;
import framework.mapping.Mapping;
import framework.mapping.VerbUrl;
import framework.util.Utilitaire;

@WebListener
public class Listener implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        try {
            ServletContext ctx = sce.getServletContext();

            String pc = ctx.getInitParameter("controller");
            String ps = ctx.getInitParameter("service");
            String pd = ctx.getInitParameter("repository");

            Conteneur conteneur = new Conteneur();
            conteneur.scanner(pc, ps, pd);

            HashMap<VerbUrl, Mapping> routes = new HashMap<>();
            Utilitaire.scannerRoutes(pc, routes);

            ctx.setAttribute("conteneur", conteneur);
            ctx.setAttribute("routes", routes);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
