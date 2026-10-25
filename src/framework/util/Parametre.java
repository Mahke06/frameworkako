package framework.util;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.Part;

import java.lang.reflect.Parameter;
import java.lang.reflect.Method;
import java.lang.reflect.Field;

import jakarta.servlet.http.HttpSession;
import framework.session.Session;


public class Parametre {

    public static Object[] recupererParametres(HttpServletRequest req, Method methode, HttpSession httpsession) throws Exception {
        Parameter[] parameters = methode.getParameters();

        Object[] arguments = new Object[parameters.length];

        for (int i = 0; i < parameters.length; i++) {

            Parameter parameter = parameters[i];

            String nom = parameter.getName();

            String valeur = req.getParameter(nom);

            
            if (parameter.getType() == Session.class) {
                Session session = new Session();
                session.charger(httpsession);
                arguments[i] = session;
            } else if (parameter.getType() == String.class) {
                arguments[i] = valeur;
            } else if (parameter.getType() == int.class) {
                arguments[i] = Integer.parseInt(valeur);
            } else if (parameter.getType() == double.class) {
                arguments[i] = Double.parseDouble(valeur);
            } else if (parameter.getType() == boolean.class) {
                arguments[i] = Boolean.parseBoolean(valeur);
            } else {
                Object objet = parameter.getType().getDeclaredConstructor().newInstance();

                for (Field champ : parameter.getType().getDeclaredFields()) {

                    Object valeurConvertie;
                    String nomSetter = "set" + champ.getName().substring(0, 1).toUpperCase() + champ.getName().substring(1);

                    if (champ.getType() == byte[].class) {
                        Part part = req.getPart(champ.getName());

                        if (part == null || part.getSize() == 0) {
                            continue;
                        }

                        valeurConvertie = part.getInputStream().readAllBytes();

                    } else {
                        String valeurChamp = req.getParameter(champ.getName());

                        if (valeurChamp == null) {
                            continue;
                        }

                        valeurConvertie = valeurChamp;

                        if (champ.getType() == int.class) {
                            valeurConvertie = Integer.parseInt(valeurChamp);
                        } else if (champ.getType() == double.class) {
                            valeurConvertie = Double.parseDouble(valeurChamp);
                        } else if (champ.getType() == boolean.class) {
                            valeurConvertie = Boolean.parseBoolean(valeurChamp);
                        }
                    }

                    for (Method m : parameter.getType().getMethods()) {
                        if (m.getName().equals(nomSetter) && m.getParameterCount() == 1
                                && m.getParameterTypes()[0] == champ.getType()) {
                            m.invoke(objet, valeurConvertie);
                            break;
                        }
                    }
                }

                arguments[i] = objet;
            }
        }

        return arguments;
    }
}