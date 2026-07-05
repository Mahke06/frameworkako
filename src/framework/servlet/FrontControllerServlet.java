package framework.servlet;

import java.io.*;
import java.lang.reflect.Method;
import java.util.HashMap;

import framework.mapping.Mapping;
import framework.mapping.VerbUrl;
import framework.util.Utilitaire;
import jakarta.servlet.*;
import jakarta.servlet.http.*;

public class FrontControllerServlet extends HttpServlet  { 
    HashMap<VerbUrl, Mapping> mappingUrls = new HashMap<>();
    HashMap<VerbUrl, Mapping> doublonUrl = new HashMap<>();

    
    @Override
    @SuppressWarnings("unchecked")
    public void init() throws ServletException {
        Object contextRoutes = getServletContext().getAttribute("routes");
        Object contextDoublons = getServletContext().getAttribute("doublons");

        if (contextRoutes instanceof HashMap && contextDoublons instanceof HashMap) {
            this.mappingUrls = (HashMap<VerbUrl, Mapping>) contextRoutes;
            this.doublonUrl = (HashMap<VerbUrl, Mapping>) contextDoublons;
            return;
        }

        String packageName = getInitParameter("controller");
        if (packageName == null) {
            packageName = getServletContext().getInitParameter("controller");
        }
        Utilitaire.scanRoutes(packageName, this.mappingUrls, this.doublonUrl);
    }

    protected void processRequest(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        res.setContentType("text/plain;charset=UTF-8");

        String path = req.getRequestURI().substring(req.getContextPath().length());     
        try {
           
            VerbUrl verbUrl = new VerbUrl(path, req.getMethod());
            if (this.doublonUrl.containsKey(verbUrl)) {
                throw new Exception("La route :" + verbUrl.getMethod() +  verbUrl.getUrl() + " existe deja");
            }
            Mapping mapping = this.mappingUrls.get(verbUrl);

            if (mapping == null) {
                res.getWriter().println("\nLien non trouvé : " + path);
                return;
            }
            
            res.getWriter().println("\nLien trouvé");
            res.getWriter().println("Controller : " + mapping.getControllerName());
            res.getWriter().println("Méthode : " + mapping.getMethodName());
            res.getWriter().println("Http Method : " + req.getMethod());

            
            Class <?> clazz = Class.forName(mapping.getControllerName());
            Object clazzInstance = clazz.getDeclaredConstructor().newInstance();
            Method m = clazz.getDeclaredMethod(mapping.getMethodName());
            m.invoke(clazzInstance);


        } catch (Exception e) {
            res.getWriter().println(e.getMessage());
            res.getWriter().println("\nLiens disponibles :");

            for (VerbUrl url : this.mappingUrls.keySet()) {
                Mapping mapping = this.mappingUrls.get(url);
                res.getWriter().println("Controller: " + mapping.getControllerName() + "\n" + 
                                        "Méthode: " + mapping.getMethodName() + "\n" +
                                        "Http Method: " + url.getMethod());
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
