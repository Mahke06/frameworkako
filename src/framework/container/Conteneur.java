package framework.container;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import framework.annotation.AccesDonnees;
import framework.annotation.Controller;
import framework.annotation.Injecter;
import framework.annotation.ServiceMetier;
import framework.util.Utilitaire;

public class Conteneur {

    private HashMap<Class<?>, Object> objets = new HashMap<>();

    public void scanner(String paquetControleurs, String paquetServices, String paquetDepots) {
        List<Class<?>> toutesLesClasses = new ArrayList<>();

        if (paquetControleurs != null) {
            toutesLesClasses.addAll(Utilitaire.trouverClasses(paquetControleurs, Controller.class));
        }
        if (paquetServices != null) {
            toutesLesClasses.addAll(Utilitaire.trouverClasses(paquetServices, ServiceMetier.class));
        }
        if (paquetDepots != null) {
            toutesLesClasses.addAll(Utilitaire.trouverClasses(paquetDepots, AccesDonnees.class));
        }

        for (Class<?> c : toutesLesClasses) {
            try {
                Object instance = c.getDeclaredConstructor().newInstance();
                objets.put(c, instance);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        for (Object instance : objets.values()) {
            injecter(instance);
        }
    }

    private void injecter(Object instance) {
        for (Field f : instance.getClass().getDeclaredFields()) {
            if (f.isAnnotationPresent(Injecter.class)) {
                Object dep = objets.get(f.getType());
                if (dep != null) {
                    try {
                        f.setAccessible(true);
                        f.set(instance, dep);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public <T> T obtenir(Class<T> clazz) {
        return (T) objets.get(clazz);
    }
}
