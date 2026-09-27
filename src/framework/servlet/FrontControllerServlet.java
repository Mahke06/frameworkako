package framework.servlet;

import java.io.*;
import java.lang.reflect.Method;
import java.util.HashMap;

import com.google.gson.Gson;

import framework.container.Conteneur;
import framework.mapping.Mapping;
import framework.mapping.VerbUrl;
import framework.modelview.ModelAndView;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import framework.annotation.WebAPI;

import framework.json.Jsonappele;

public class FrontControllerServlet extends HttpServlet {
    HashMap<VerbUrl, Mapping> routesUrl = new HashMap<>();
    Conteneur conteneur;
    private static final Gson json = new Gson();

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

            if(methode.isAnnotationPresent(WebAPI.class)){
                Jsonappele.manoratraJson(res, methode.invoke(ctrl));
                return;
            }
            else {
                Jsonappele.dispatcher(req, res, methode.invoke(ctrl));
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
