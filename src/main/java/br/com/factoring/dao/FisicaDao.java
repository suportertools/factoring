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
        if (pf.getDescricao().isEmpty()) {
            return new ArrayList();
        }

        List<String> list_where = new ArrayList();

        switch (pf.getPorPesquisa()) {
            case "nome":
                list_where.add("TRANSLATE(LOWER(p.ds_nome)) LIKE '%" + AnaliseString.normalizeLower(pf.getDescricao()) + "%'");
                break;
            case "cpf":
                list_where.add("p.ds_documento = '" + pf.getDescricao() + "'");
                break;
        }

        String WHERE = "";

        for (String w : list_where) {
            if (WHERE.isEmpty()) {
                WHERE = " WHERE " + w + " \n ";
            } else {
                WHERE = " AND " + w + " \n ";
            }
        }

        try {
            Query qry = getEntityManager().createNativeQuery(
                    " SELECT f.* " + " \n "
                    + " FROM pes_fisica f " + " \n "
                    + "INNER JOIN pes_pessoa p ON p.id = f.id_pessoa " + " \n "
                    + WHERE
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
