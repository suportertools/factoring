/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.factoring.dao;

import br.com.factoring.connections.DB;
import br.com.factoring.model.Grupo;
import br.com.factoring.model.Pagina;
import br.com.factoring.model.PermissaoGrupo;
import br.com.factoring.utils.AnaliseString;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Query;

/**
 *
 * @author Claudemir Rtools
 */
public class PermissaoDao extends DB {

    public List<Object[]> listaPermissaoUsuario(Integer id_grupo, Integer id_usuario, String descricao_permissao) {
        String text
                = "SELECT p.* \n"
                + "  FROM seg_permissao_grupo pg \n "
                + " INNER JOIN seg_permissao p ON p.id = pg.id_permissao \n "
                + " WHERE pg.id_grupo = " + id_grupo + " \n "
                + "   AND p.ds_acao = '" + descricao_permissao + "' \n "
                + "UNION \n "
                + "SELECT p.* \n "
                + "  FROM seg_permissao_usuario pu \n "
                + " INNER JOIN seg_permissao p ON p.id = pu.id_permissao \n "
                + " WHERE pu.id_usuario = " + id_usuario + " \n "
                + "   AND p.ds_acao = '" + descricao_permissao + "'";
        try {
            Query qry = getEntityManager().createNativeQuery(
                    text
            );
            return qry.getResultList();
        } catch (Exception e) {
            e.getMessage();
        }
        return new ArrayList();
    }

    public List<Grupo> listaGrupo() {
        try {
            Query qry = getEntityManager().createNativeQuery(
                    "SELECT g.* \n "
                    + "  FROM seg_grupo g \n "
                    + " ORDER BY g.ds_nome", Grupo.class
            );
            return qry.getResultList();
        } catch (Exception e) {
            e.getMessage();
        }
        return new ArrayList();
    }

    public List<Pagina> listaPagina(String descricao_pesquisa) {
        try {
            String text;

            if (descricao_pesquisa.isEmpty()) {
                text
                        = "SELECT p.* \n"
                        + "  FROM seg_pagina p \n"
                        + " ORDER BY p.ds_nome";
            } else {
                descricao_pesquisa = "%" + AnaliseString.normalizeLower(descricao_pesquisa) + "%";
                text
                        = "SELECT p.* \n"
                        + "  FROM seg_pagina p \n"
                        + " WHERE TRANSLATE(LOWER(p.ds_nome)) LIKE '" + descricao_pesquisa + "' \n"
                        + "UNION \n"
                        + " SELECT p.* \n"
                        + "  FROM seg_permissao pe \n"
                        + " INNER JOIN seg_pagina p ON p.id = pe.id_pagina \n"
                        + " WHERE TRANSLATE(LOWER(pe.ds_nome)) LIKE '" + descricao_pesquisa + "'"
                        + " ORDER BY 1";
            }

            Query qry = getEntityManager().createNativeQuery(text, Pagina.class);
            return qry.getResultList();
        } catch (Exception e) {
            e.getMessage();
        }
        return new ArrayList();
    }

    public List<Object[]> listaPermissaoGrupo(Integer id_grupo, Integer id_pagina) {
        try {
            Query qry = getEntityManager().createNativeQuery(
                    "SELECT p.id, pa.ds_nome, true \n"
                    + "  FROM seg_permissao p \n"
                    + " INNER JOIN seg_pagina pa ON pa.id = p.id_pagina \n"
                    + " WHERE p.id IN (SELECT pg.id_permissao FROM seg_permissao_grupo pg WHERE pg.id_grupo = " + id_grupo + ") \n"
                    + "   AND p.id_pagina = " + id_pagina + " \n"
                    + "UNION \n"
                    + "SELECT p.id, pa.ds_nome, false \n"
                    + "  FROM seg_permissao p \n"
                    + " INNER JOIN seg_pagina pa ON pa.id = p.id_pagina \n"
                    + " WHERE p.id NOT IN (SELECT pg.id_permissao FROM seg_permissao_grupo pg WHERE pg.id_grupo = " + id_grupo + ") \n"
                    + "   AND p.id_pagina = " + id_pagina + " \n"
                    + "ORDER BY 1, 2"
            );

            return qry.getResultList();
        } catch (Exception e) {
            e.getMessage();
        }
        return new ArrayList();
    }

    public PermissaoGrupo pesquisaPermissaoGrupo(Integer id_grupo, Integer id_permissao) {
        try {
            Query qry = getEntityManager().createNativeQuery(
                    "SELECT pg.* \n"
                    + "  FROM seg_permissao_grupo pg \n"
                    + " WHERE pg.id_grupo = " + id_grupo + "\n"
                    + "   AND pg.id_permissao = " + id_permissao + "\n"
                    + " ", PermissaoGrupo.class
            );

            return (PermissaoGrupo) qry.getSingleResult();
        } catch (Exception e) {
            e.getMessage();
        }
        return null;
    }
}
