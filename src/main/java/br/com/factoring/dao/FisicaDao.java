/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.factoring.dao;

import br.com.factoring.connections.DB;
import br.com.factoring.controller.FisicaController;
import br.com.factoring.controller.FisicaController.PesquisaEmpresa;
import br.com.factoring.model.Fisica;
import br.com.factoring.model.Juridica;
import br.com.factoring.utils.AnaliseString;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Query;

/**
 *
 * @author Claudemir Rtools
 */
public class FisicaDao extends DB {

    public List<Fisica> listaPesquisaFisica(FisicaController.PesquisaFisica pf) {
        if (pf.getNome().isEmpty()){
            return new ArrayList();
        }
        
        try {
            Query qry = getEntityManager().createNativeQuery(
                    " SELECT f.* "+ " \n "
                    + " FROM pes_fisica f "+ " \n "
                    + "INNER JOIN pes_pessoa p ON p.id = f.id_pessoa "+ " \n "
                    + "WHERE TRANSLATE(LOWER(p.ds_nome)) LIKE '%" + AnaliseString.normalizeLower(pf.getNome()) + "%' \n "
                    + "ORDER BY p.ds_nome \n "
                    + "LIMIT 30", Fisica.class
            );

            return qry.getResultList();
        } catch (Exception e) {
            e.getMessage();
        }
        return new ArrayList();
    }
}
