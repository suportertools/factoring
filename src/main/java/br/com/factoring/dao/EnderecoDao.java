/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.factoring.dao;

import br.com.factoring.connections.DB;
import br.com.factoring.model.Cidade;
import br.com.factoring.model.Endereco;
import br.com.factoring.model.TipoEndereco;
import br.com.factoring.utils.AnaliseString;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Query;

/**
 *
 * @author Claudemir Rtools
 */
public class EnderecoDao extends DB {

    public List<Endereco> listaEndereco(Integer id_pessoa) {
        try {
            Query qry = getEntityManager().createNativeQuery(
                    " SELECT e.* " + " \n "
                    + " FROM pes_endereco e " + " \n "
                    + "INNER JOIN pes_pessoa p ON p.id = e.id_pessoa " + " \n "
                    + "WHERE p.id = " + id_pessoa + " \n " + " \n "
                    + "ORDER BY p.ds_nome", Endereco.class
            );

            return qry.getResultList();
        } catch (Exception e) {
            e.getMessage();
        }
        return new ArrayList();
    }

    public Cidade pesquisaCidadeUF(String uf, String cidade) {
        Query query = getEntityManager().createNativeQuery(
                " SELECT c.* \n "
                + " FROM pes_cidade c \n "
                + "WHERE LOWER(TRANSLATE(c.ds_cidade)) = '" + AnaliseString.normalizeLower(cidade) + "' \n "
                + "  AND LOWER(c.ds_uf) = '" + uf.toLowerCase() + "'", Cidade.class);

        try {
            return (Cidade) query.getSingleResult();
        } catch (Exception e) {
            e.getMessage();
        }
        return null;
    }

    public List<TipoEndereco> listaTipoEndereco() {
        Query query = getEntityManager().createNativeQuery(
                " SELECT te.* \n "
                + " FROM pes_tipo_endereco te \n "
                + "ORDER BY te.id", TipoEndereco.class
        );

        try {
            return query.getResultList();
        } catch (Exception e) {
            e.getMessage();
        }
        return new ArrayList();
    }
}
