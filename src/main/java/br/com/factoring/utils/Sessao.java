/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.factoring.utils;

import javax.faces.context.FacesContext;


/**
 *
 * @author Claudemir Rtools
 */
public class Sessao {

    public static void put(String session_name, Object session_value) {
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(session_name, session_value);
    }

    public static Object get(String session_name) {
        return get(session_name, false);
    }

    public static Object get(String session_name, boolean remove) {
        Object ob = FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get(session_name);
        if (remove) {
            remove(session_name);
        }
        return ob;
    }

    public static void remove(String session_name) {
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().remove(session_name);
    }

    public static boolean exist(String session_name) {
        return FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get(session_name) != null;
    }

}
