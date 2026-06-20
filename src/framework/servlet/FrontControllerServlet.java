package framework.servlet;

import java.io.*;
import java.util.List;

import framework.util.Utilitaire;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.util.ArrayList;

public class FrontControllerServlet extends HttpServlet  { 
    List<Class<?>> listController = new ArrayList<>();

    @Override
    public void init() throws ServletException {
        String packageName = getInitParameter("controller");
        listController = Utilitaire.findControllers(packageName);
    }

    protected void processRequest(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        res.getWriter().println("URL: " + req.getRequestURL());
        res.getWriter().println("URI: " + req.getRequestURI()); 
        res.getWriter().println("Context Path: " + req.getContextPath());
        res.getWriter().println("Path: " + req.getRequestURI().substring(req.getContextPath().length()));     
        for (Class<?> clazz : this.listController) {
            res.getWriter().println(clazz.getName());
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