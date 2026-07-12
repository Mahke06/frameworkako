package framework.modelview;

import java.util.HashMap;

public class ModelAndView {

    private String page;
    private HashMap<String, Object> valeurs;

    public ModelAndView(String page) {
        this.page = page;
        this.valeurs = new HashMap<>();
    }

    public void ajouterValeur(String cle, Object valeur) {
        this.valeurs.put(cle, valeur);
    }

    public String getPage() {
        return page;
    }

    public void setPage(String page) {
        this.page = page;
    }

    public HashMap<String, Object> getValeurs() {
        return valeurs;
    }
}
