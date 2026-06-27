package framework.util;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import framework.annotation.Controller;
import framework.annotation.UrlMapping;
import framework.mapping.Mapping;

public class Utilitaire {

    public static List<Class<?>> findControllers(String packageName) {
        List<Class<?>> controllers = new ArrayList<>();

        try {
            String path = packageName.replace('.', '/');

            URL resource = Thread.currentThread().getContextClassLoader().getResource(path);

            if (resource == null) {
                return controllers;
            }

            File directory = new File(resource.toURI());

            File[] files = directory.listFiles();

            if (files == null) {
                return controllers;
            }

            for (File file : files) {

                if (file.getName().endsWith(".class")) {

                    String className = packageName + "." + file.getName().replace(".class", "");

                    Class<?> clazz = Class.forName(className);

                    if (clazz.isAnnotationPresent(Controller.class)) {
                        controllers.add(clazz);
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return controllers;
    }




    public static void scanRoutes(String packageName, HashMap<String, Mapping> routes) {
        for (Class<?> clazz : findControllers(packageName)) {
            for (Method method : clazz.getDeclaredMethods()) {
                if (method.isAnnotationPresent(UrlMapping.class)) {
                    String url = method.getAnnotation(UrlMapping.class).value();
                    routes.put(url, new Mapping(clazz.getName(), method.getName()));
                }
            }
    }
}
}