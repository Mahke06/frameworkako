package framework.servlet;

import java.io.*;
import java.util.List;

import framework.mapping.Mapping;
import framework.util.Utilitaire;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.util.ArrayList;
import java.util.HashMap;

public class FrontControllerServlet extends HttpServlet  { 
    List<Class<?>> listController = new ArrayList<>();
    HashMap<String, Mapping> mappingUrls = new HashMap<>();
    
    @Override
    public void init() throws ServletException {
        String packageName = getInitParameter("controller");
        listController = Utilitaire.findControllers(packageName);
        Utilitaire.scanRoutes(packageName, this.mappingUrls);
     
    }

    protected void processRequest(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        res.setContentType("text/plain;charset=UTF-8");
        res.getWriter().println("URL: " + req.getRequestURL());
        res.getWriter().println("URI: " + req.getRequestURI()); 
        res.getWriter().println("Context Path: " + req.getContextPath());
        res.getWriter().println("Path: " + req.getRequestURI().substring(req.getContextPath().length()));  
        
        
        res.getWriter().println("\nListe des classes des contrôleurs:");
        for (Class<?> clazz : this.listController) {
            res.getWriter().println(clazz.getName());
        }


        String path = req.getRequestURI().substring(req.getContextPath().length());     
        try {
           
            Mapping mapping = this.mappingUrls.get(path);

            res.getWriter().println("\nLien trouvé");
            res.getWriter().println("Controller : " + mapping.getControllerName());
            res.getWriter().println("Méthode : " + mapping.getMethodName());

        } catch (Exception e) {
            res.getWriter().println("\nLiens disponibles :");

            for (String url : this.mappingUrls.keySet()) {
                Mapping mapping = this.mappingUrls.get(url);
                res.getWriter().println("URL: " + url + "\n" + 
                                        "Controller: " + mapping.getControllerName() + "\n" + 
                                        "Méthode: " + mapping.getMethodName());
            }
        }
    }


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException  {
        processRequest(req, res);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException  {
        processRequest(req, res);
    }
}