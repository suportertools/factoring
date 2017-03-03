/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.factoring.dao;

import br.com.factoring.connections.DB;
import br.com.factoring.model.Usuario;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Query;

/**
 *
 * @author Claudemir Rtools
 */
public class UsuarioDao extends DB {

    public Usuario pesquisaUsuario(String usuario, String senha) {
        try {
            Query qry = getEntityManager().createNativeQuery(
                    "SELECT u.* FROM seg_usuario u WHERE lower(u.ds_login) = '" + usuario + "' AND u.ds_senha = '" + senha + "'", Usuario.class
            );

            return (Usuario) qry.getSingleResult();
        } catch (Exception e) {
            e.getMessage();
        }
        return null;
    }

    public Usuario pesquisaUsuarioLoginExiste(Integer id_usuario, String usuario) {
        String text
                = "SELECT u.* \n"
                + "  FROM seg_usuario u \n"
                + (id_usuario != null ? " WHERE u.id <> " + id_usuario + " AND u.ds_login = '" + usuario + "'" : " WHERE u.ds_login = '" + usuario + "'");
        try {
            Query qry = getEntityManager().createNativeQuery(
                    text, Usuario.class
            );

            return (Usuario) qry.getSingleResult();
        } catch (Exception e) {
            e.getMessage();
        }
        return null;
    }

    public List<Usuario> listaUsuario() {
        try {
            Query qry = getEntityManager().createNativeQuery(
                    "SELECT u.* FROM seg_usuario u ORDER BY u.ds_nome", Usuario.class
            );

            return qry.getResultList();
        } catch (Exception e) {
            e.getMessage();
        }
        return new ArrayList();
    }
}
