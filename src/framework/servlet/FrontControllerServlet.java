package framework.servlet;

import java.io.*;
import java.lang.reflect.Method;
import java.util.HashMap;

import framework.mapping.Mapping;
import framework.mapping.VerbUrl;
import framework.modelview.ModelAndView;
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
        String chemin = req.getRequestURI().substring(req.getContextPath().length());

        try {

            VerbUrl verbeUrl = new VerbUrl(chemin, req.getMethod());
            if (this.doublonUrl.containsKey(verbeUrl)) {
                throw new Exception("La route :" + verbeUrl.getMethod() +  verbeUrl.getUrl() + " existe deja");
            }
            Mapping mapping = this.mappingUrls.get(verbeUrl);

            if (mapping == null) {
                res.setContentType("text/plain;charset=UTF-8");
                res.getWriter().println("\nLien non trouvé : " + chemin);
                return;
            }

            Class <?> classeControleur = Class.forName(mapping.getControllerName());
            Object objetControleur = classeControleur.getDeclaredConstructor().newInstance();
            Method methode = classeControleur.getDeclaredMethod(mapping.getMethodName());
            Object resultat = methode.invoke(objetControleur);

            if (resultat instanceof ModelAndView) {
                ModelAndView modelAndView = (ModelAndView) resultat;

                for (String cle : modelAndView.getValeurs().keySet()) {
                    req.setAttribute(cle, modelAndView.getValeurs().get(cle));
                }

                RequestDispatcher dispatcher = req.getRequestDispatcher(modelAndView.getPage());
                dispatcher.forward(req, res);
            }

        } catch (Exception e) {
            res.setContentType("text/plain;charset=UTF-8");
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
