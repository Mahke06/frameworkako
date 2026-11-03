package framework.servlet;

import java.io.*;
import java.lang.reflect.Method;
import java.util.HashMap;

import framework.container.Conteneur;
import framework.mapping.Mapping;
import framework.mapping.VerbUrl;
import jakarta.servlet.*;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.http.*;
import framework.annotation.WebAPI;
import framework.json.Jsonappele;
import framework.util.Parametre;

import framework.session.Session;

import framework.annotation.Auth;

import java.util.Arrays;

@MultipartConfig 
public class FrontControllerServlet extends HttpServlet {

    HashMap<VerbUrl, Mapping> routesUrl = new HashMap<>();
    Conteneur conteneur;
    private String authSessionAttribute;
    private String roleSessionAttribute;
    

    @Override
    public void init() throws ServletException {
        
        Object r = getServletContext().getAttribute("routes");
        Object c = getServletContext().getAttribute("conteneur");

        String chemin = getServletContext().getInitParameter("upload");
        System.out.println(chemin);

        authSessionAttribute = getInitParameter("auth-session-attribute");
        roleSessionAttribute = getInitParameter("role-session-attribute");
        if (authSessionAttribute == null || authSessionAttribute.isEmpty()) {
            throw new ServletException("Parametre auth-session-attribute manquant");
        }

        if (r instanceof HashMap && c instanceof Conteneur) {
            routesUrl = (HashMap<VerbUrl, Mapping>) r;
            conteneur = (Conteneur) c;
        }
    }

    protected void processRequest(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {

        String chemin = req.getRequestURI().substring(req.getContextPath().length());
        HttpSession httpsession = req.getSession();

        try {
            VerbUrl vUrl = new VerbUrl(chemin, req.getMethod());
            Mapping m = routesUrl.get(vUrl);

            if (m == null) {
                res.getWriter().println("Lien non trouve : " + chemin);
                return;
            }

            Class<?> clazz = Class.forName(m.getControllerName());
            Object ctrl = conteneur.obtenir(clazz);

            Method methode = null;

            for (Method method : clazz.getDeclaredMethods()) {
                if (method.getName().equals(m.getMethodName())) {
                    methode = method;
                    break;
                }
            }

            if (methode == null) {
                res.getWriter().println("Methode non trouvee");
                return;
            }

            if (methode.isAnnotationPresent(Auth.class)) {

                Auth auth = methode.getAnnotation(Auth.class);

                HttpSession session = req.getSession(false);

                if (session == null || session.getAttribute(authSessionAttribute) == null) {
                    res.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Authentification requise");
                    return;
                }

                String[] rolesAutorises = auth.roles();

                if (rolesAutorises.length > 0) {

                    if (roleSessionAttribute == null || roleSessionAttribute.isEmpty()) {
                        throw new ServletException("init-param role-session-attribute manquant");
                    }

                    Object roleUtilisateur = session.getAttribute(roleSessionAttribute);

                    boolean autorise = roleUtilisateur != null && Arrays.asList(rolesAutorises).contains(roleUtilisateur.toString());

                    if (!autorise) {
                        res.sendError(HttpServletResponse.SC_FORBIDDEN, "Rôle non autorisé");
                        return;
                    }
                }
            }

            Object[] arguments = Parametre.recupererParametres(req, methode, httpsession);

            Object resultat = methode.invoke(ctrl, arguments);

            for (Object argument : arguments) {
                if (argument instanceof Session) {
                    ((Session) argument).mettreAJour(httpsession);
                }
            }

            if (methode.isAnnotationPresent(WebAPI.class)) {
                Jsonappele.manoratraJson(res,resultat);
                return;
            }
            
            Jsonappele.dispatcher(req,res,resultat);

        } catch (Exception e) {
            res.getWriter().println(e.getMessage());
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException { processRequest(req, res);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException { processRequest(req, res);
    }
}