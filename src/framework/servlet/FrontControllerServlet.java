package framework.servlet;

import java.io.*;
import java.lang.reflect.Method;
import java.util.HashMap;

import framework.container.Conteneur;
import framework.mapping.Mapping;
import framework.mapping.VerbUrl;
import framework.modelview.ModelAndView;
import jakarta.servlet.*;
import jakarta.servlet.http.*;

public class FrontControllerServlet extends HttpServlet {

    HashMap<VerbUrl, Mapping> routesUrl = new HashMap<>();
    Conteneur conteneur;

    @Override
    public void init() throws ServletException {
        Object r = getServletContext().getAttribute("routes");
        Object c = getServletContext().getAttribute("conteneur");

        if (r instanceof HashMap && c instanceof Conteneur) {
            routesUrl = (HashMap<VerbUrl, Mapping>) r;
            conteneur = (Conteneur) c;
        }
    }

    protected void processRequest(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        String chemin = req.getRequestURI().substring(req.getContextPath().length());

        try {
            VerbUrl vUrl = new VerbUrl(chemin, req.getMethod());
            Mapping m = routesUrl.get(vUrl);

            if (m == null) {
                res.getWriter().println("Lien non trouve : " + chemin);
                return;
            }

            Class<?> clazz = Class.forName(m.getControllerName());
            Object ctrl = conteneur.obtenir(clazz);
            Method methode = clazz.getDeclaredMethod(m.getMethodName());
            Object resultat = methode.invoke(ctrl);

            if (resultat instanceof ModelAndView) {
                ModelAndView mv = (ModelAndView) resultat;

                for (String cle : mv.getValeurs().keySet()) {
                    req.setAttribute(cle, mv.getValeurs().get(cle));
                }

                RequestDispatcher rd = req.getRequestDispatcher(mv.getPage());
                rd.forward(req, res);
            }

        } catch (Exception e) {
            res.getWriter().println(e.getMessage());
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        processRequest(req, res);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        processRequest(req, res);
    }
}
