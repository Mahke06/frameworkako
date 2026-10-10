package framework.util;

import jakarta.servlet.http.HttpServletRequest;

import java.lang.reflect.Parameter;
import java.lang.reflect.Method;

public class Parametre {

    public static Object[] recupererParametres(HttpServletRequest req, Method methode) {
        Parameter[] parameters = methode.getParameters();

        Object[] arguments = new Object[parameters.length];

        for (int i = 0; i < parameters.length; i++) {

            Parameter parameter = parameters[i];

            String nom = parameter.getName();

            String valeur = req.getParameter(nom);

            if (valeur == null) {
                arguments[i] = null;
            } else if (parameter.getType() == String.class) {
                arguments[i] = valeur;
            } else if (parameter.getType() == int.class) {
                arguments[i] = Integer.parseInt(valeur);
            } else if (parameter.getType() == double.class) {
                arguments[i] = Double.parseDouble(valeur);
            } else if (parameter.getType() == boolean.class) {
                arguments[i] = Boolean.parseBoolean(valeur);
            } else {
                arguments[i] = null;
            }
        }

        return arguments;
    }
}