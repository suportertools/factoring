/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.factoring.dao;

import br.com.factoring.connections.DB;
import br.com.factoring.find.PesquisaEmitenteFind;
import br.com.factoring.find.PesquisaPessoaFind;
import br.com.factoring.model.Emitente;
import br.com.factoring.model.Pessoa;
import br.com.factoring.model.TipoDocumento;
import br.com.factoring.utils.AnaliseString;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Query;

/**
 *
 * @author Claudemir Rtools
 */
public class PessoaDao extends DB {

    public List<Pessoa> listaPessoa(PesquisaPessoaFind pp) {
        if (pp.getNome().isEmpty()) {
            return new ArrayList();
        }

        try {
            Query qry = getEntityManager().createNativeQuery(
                    " SELECT p.* \n "
                    + " FROM pes_pessoa p \n "
                    + "WHERE TRANSLATE(LOWER(p.ds_nome)) LIKE '%" + AnaliseString.normalizeLower(pp.getNome()) + "%' \n "
                    + "ORDER BY p.ds_nome \n "
                    + "LIMIT 30", Pessoa.class
            );

            return qry.getResultList();
        } catch (Exception e) {
            e.getMessage();
        }
        return new ArrayList();
    }

    public List<TipoDocumento> listaTipoDocumento() {
        try {
            Query qry = getEntityManager().createNativeQuery(
                    " SELECT td.* \n "
                    + " FROM pes_tipo_documento td \n "
                    + "ORDER BY td.id", TipoDocumento.class
            );

            return qry.getResultList();
        } catch (Exception e) {
            e.getMessage();
        }
        return new ArrayList();
    }

    public Pessoa pesquisaPessoaDocumento(Integer id_pessoa, String documento) {
        String AND = id_pessoa != null ? " AND p.id <> " + id_pessoa : "";
        try {
            Query qry = getEntityManager().createNativeQuery(
                    " SELECT p.* \n "
                    + "  FROM pes_pessoa p \n "
                    + " WHERE p.ds_documento LIKE '" + documento + "'"
                    + AND, Pessoa.class
            );

            return (Pessoa) qry.getSingleResult();
        } catch (Exception e) {
            e.getMessage();
        }
        return null;
    }

    public List<Emitente> listaEmitente(PesquisaEmitenteFind pe) {
        if (pe.getNome().isEmpty()) {
            return new ArrayList();
        }

        try {
            Query qry = getEntityManager().createNativeQuery(
                    " SELECT e.* \n "
                    + " FROM pes_emitente e \n "
                    + "WHERE TRANSLATE(LOWER(e.ds_nome)) LIKE '%" + AnaliseString.normalizeLower(pe.getNome()) + "%' \n "
                    + "ORDER BY e.ds_nome \n "
                    + "LIMIT 30", Emitente.class
            );

            return qry.getResultList();
        } catch (Exception e) {
            e.getMessage();
        }
        return new ArrayList();
    }

    public Emitente pesquisaEmitenteDocumento(Integer id_emitente, String documento) {
        String AND = id_emitente != null ? " AND e.id <> " + id_emitente : "";
        try {
            Query qry = getEntityManager().createNativeQuery(
                    " SELECT e.* \n "
                    + "  FROM pes_emitente e \n "
                    + " WHERE e.ds_documento LIKE '" + documento + "'"
                    + AND, Emitente.class
            );

            return (Emitente) qry.getSingleResult();
        } catch (Exception e) {
            e.getMessage();
        }
        return null;
    }

    public Pessoa pessoaDefault() {
        try {
            Query qry = getEntityManager().createNativeQuery(
                    " SELECT p.* \n "
                    + "  FROM pes_pessoa p \n "
                    + " WHERE p.id = 1", Pessoa.class
            );

            return (Pessoa) qry.getSingleResult();
        } catch (Exception e) {
            e.getMessage();
        }
        return null;
    }
}
