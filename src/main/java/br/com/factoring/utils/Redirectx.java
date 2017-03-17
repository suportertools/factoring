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
public class Redirectx {

    public static void go(String page) {
        try {
            FacesContext.getCurrentInstance().getExternalContext().redirect(page + ".xhtml");
        } catch (Exception e) {
            e.getMessage();
        }
    }
}
