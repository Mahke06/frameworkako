package framework.util;

import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import framework.annotation.Controller;
import framework.annotation.UrlMapping;
import framework.mapping.Mapping;
import framework.mapping.VerbUrl;

public class Utilitaire {

    public static List<Class<?>> trouverClasses(String nomPaquet, Class<? extends Annotation> annotation) {
        List<Class<?>> classes = new ArrayList<>();

        if (nomPaquet == null) {
            return classes;
        }

        try {
            String path = nomPaquet.replace('.', '/');
            URL ressource = Thread.currentThread().getContextClassLoader().getResource(path);

            if (ressource == null) {
                return classes;
            }

            File dir = new File(ressource.toURI());
            File[] files = dir.listFiles();

            if (files == null) {
                return classes;
            }

            for (File f : files) {
                if (f.getName().endsWith(".class")) {
                    String nom = nomPaquet + "." + f.getName().replace(".class", "");
                    Class<?> c = Class.forName(nom);

                    if (c.isAnnotationPresent(annotation)) {
                        classes.add(c);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return classes;
    }

    public static void scannerRoutes(String nomPaquet, HashMap<VerbUrl, Mapping> routes) {
        for (Class<?> c : trouverClasses(nomPaquet, Controller.class)) {
            for (Method m : c.getDeclaredMethods()) {
                if (m.isAnnotationPresent(UrlMapping.class)) {
                    String url = m.getAnnotation(UrlMapping.class).value();
                    String httpMethod = m.getAnnotation(UrlMapping.class).method();
                    routes.put(new VerbUrl(url, httpMethod), new Mapping(c.getName(), m.getName()));
                }
            }
        }
    }
}
