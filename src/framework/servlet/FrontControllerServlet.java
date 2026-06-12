package framework.servlet;

import java.io.*;
import jakarta.servlet.*;
import jakarta.servlet.http.*;

public class FrontControllerServlet extends HttpServlet  { 
    protected void processRequest(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        res.getWriter().println("URL: " + req.getRequestURL());
        res.getWriter().println("URI: " + req.getRequestURI()); 
        res.getWriter().println("Context Path: " + req.getContextPath());
        res.getWriter().println("Path: " + req.getRequestURI().substring(req.getContextPath().length()));     
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