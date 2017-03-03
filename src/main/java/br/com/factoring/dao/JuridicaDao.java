/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.factoring.dao;

import br.com.factoring.connections.DB;
import br.com.factoring.controller.FisicaController;
import br.com.factoring.controller.JuridicaController;
import br.com.factoring.model.Juridica;
import br.com.factoring.utils.AnaliseString;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Query;

/**
 *
 * @author Claudemir Rtools
 */
public class JuridicaDao extends DB {

    public List<Juridica> listaPesquisaEmpresa(FisicaController.PesquisaEmpresa pe) {
        if (pe.getNome().isEmpty()) {
            return new ArrayList();
        }

        try {
            Query qry = getEntityManager().createNativeQuery(
                    " SELECT j.* \n "
                    + " FROM pes_juridica j  \n "
                    + "INNER JOIN pes_pessoa p ON p.id = j.id_pessoa \n "
                    + "WHERE TRANSLATE(LOWER(p.ds_nome)) LIKE '%" + AnaliseString.normalizeLower(pe.getNome()) + "%' \n "
                    + "ORDER BY p.ds_nome \n "
                    + "LIMIT 15", Juridica.class
            );

            return qry.getResultList();
        } catch (Exception e) {
            e.getMessage();
        }
        return new ArrayList();
    }

    public List<Juridica> listaPesquisaJuridica(JuridicaController.PesquisaJuridica pj) {
        if (pj.getNome().isEmpty()) {
            return new ArrayList();
        }

        try {
            Query qry = getEntityManager().createNativeQuery(
                    " SELECT j.* \n "
                    + " FROM pes_juridica j  \n "
                    + "INNER JOIN pes_pessoa p ON p.id = j.id_pessoa \n "
                    + "WHERE TRANSLATE(LOWER(p.ds_nome)) LIKE '%" + AnaliseString.normalizeLower(pj.getNome()) + "%' \n "
                    + "ORDER BY p.ds_nome \n "
                    + "LIMIT 15", Juridica.class
            );

            return qry.getResultList();
        } catch (Exception e) {
            e.getMessage();
        }
        return new ArrayList();
    }

}
