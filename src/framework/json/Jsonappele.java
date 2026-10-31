package framework.json;

import com.google.gson.Gson;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.ServletException;
import framework.modelview.ModelAndView;

public class Jsonappele {
    public static final Gson json = new Gson();

    public static void manoratraJson(HttpServletResponse res, Object resultat) throws IOException {
        res.setContentType("application/json");
        res.setCharacterEncoding("UTF-8");

        String reponseJson = "";
        if (resultat instanceof String){
            reponseJson = (String) resultat;
        }
        else {
            reponseJson = json.toJson(resultat);
        }

        PrintWriter reponseFinal = res.getWriter();
        reponseFinal.print(reponseJson);
        reponseFinal.flush();
    }

    public static void dispatcher(HttpServletRequest req, HttpServletResponse res, Object resultat) throws ServletException, IOException {
        if (resultat instanceof ModelAndView) {
            ModelAndView mv = (ModelAndView) resultat;

            for (String cle : mv.getValeurs().keySet()) {
                req.setAttribute(cle, mv.getValeurs().get(cle));
            }
            req.getRequestDispatcher(mv.getPage()).forward(req, res);
        }
    }
}
