package framework.listener;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import java.util.HashMap;

import framework.mapping.Mapping;
import framework.mapping.VerbUrl;
import framework.util.Utilitaire;

@WebListener
public class Listener implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        try {

            ServletContext context = sce.getServletContext();
            String packageName = context.getInitParameter("controller");

            if (packageName == null || packageName.trim().isEmpty()) {
                return;
            }

            HashMap<VerbUrl, Mapping> routes = new HashMap<>();
            HashMap<VerbUrl, Mapping> doublons = new HashMap<>();

            Utilitaire.scanRoutes(packageName, routes, doublons);

            context.setAttribute("routes", routes);
            context.setAttribute("doublons", doublons);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
