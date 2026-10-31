package framework.session;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import jakarta.servlet.http.HttpSession;

public class Session {
    private Map<String, Object> valeurs = new HashMap<>();

    public void setAttribute(String nom, Object valeur){
        valeurs.put(nom, valeur);
    }

    public Object getAttribute(String nom){
        return valeurs.get(nom);
    }

    public void removeAttribute(String nom){
        valeurs.remove(nom);
    }

    public void charger(HttpSession session) {
        Enumeration<String> noms = session.getAttributeNames();

        while (noms.hasMoreElements()) {
            String nom = noms.nextElement();
            valeurs.put(nom, session.getAttribute(nom));
        }
    }

    public void mettreAJour(HttpSession session) {
        for (Map.Entry<String, Object> entree : valeurs.entrySet()) {
            session.setAttribute(entree.getKey(), entree.getValue());
        }
    }

}
