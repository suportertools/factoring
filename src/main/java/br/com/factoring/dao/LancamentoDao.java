/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.factoring.dao;

import br.com.factoring.connections.DB;
import br.com.factoring.controller.LancamentoController;
import br.com.factoring.model.Banco;
import br.com.factoring.model.Emitente;
import br.com.factoring.model.FTipoDocumento;
import br.com.factoring.model.Movimento;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Query;

/**
 *
 * @author Claudemir Rtools
 */
public class LancamentoDao extends DB {

    public List<Movimento> listaMovimento(LancamentoController.PesquisaLancamento pl) {
        if (pl.getPessoa().getId() == -1) {
            return new ArrayList();
        }

        List<String> list_where = new ArrayList();

        list_where.add("m.id_pessoa = " + pl.getPessoa().getId());

        switch (pl.getTipoVencimento()) {
            case "a_vencer":
                list_where.add("m.dt_vencimento >= CURRENT_DATE");
                break;
            case "vencendo_hoje":
                list_where.add("m.dt_vencimento = CURRENT_DATE");
                break;
            case "vencido_a_6_meses":
                list_where.add("m.dt_vencimento >= CURRENT_DATE - 180");
                break;
            case "vencido_todos":
                list_where.add("m.dt_vencimento < CURRENT_DATE");
                break;
            default:
                break;
        }

        switch (pl.getIndexListaTipoDocumento()) {
            case 0:
                break;
            default:
                list_where.add("m.id_tipo_documento = " + Integer.valueOf(pl.getListaTipoDocumento().get(pl.getIndexListaTipoDocumento()).getDescription()));
                break;
        }

        String WHERE = "";

        for (String sw : list_where) {
            if (WHERE.isEmpty()) {
                WHERE = " WHERE " + sw + " \n ";
            } else {
                WHERE += " AND " + sw + " \n ";
            }
        }

        String ORDER = "ORDER BY ";

        switch (pl.getOrdem()) {
            case "lancamento":
                ORDER += "m.dt_lancamento DESC \n ";
                break;
            case "vencimento":
                ORDER += "m.dt_vencimento DESC \n ";
                break;
            default:
                ORDER += "m.id \n ";
                break;
        }

        try {
            Query qry = getEntityManager().createNativeQuery(
                    " SELECT m.* \n "
                    + " FROM fin_movimento m \n "
                    + WHERE
                    + ORDER
                    + "LIMIT 15", Movimento.class
            );

            return qry.getResultList();
        } catch (Exception e) {
            e.getMessage();
        }
        return new ArrayList();
    }

    public List<FTipoDocumento> listaTipoDocumento() {
        try {
            Query qry = getEntityManager().createNativeQuery(
                    " SELECT td.* \n "
                    + " FROM fin_tipo_documento td \n "
                    + "ORDER BY td.ds_tipo_documento", FTipoDocumento.class
            );

            return qry.getResultList();
        } catch (Exception e) {
            e.getMessage();
        }
        return new ArrayList();
    }

    public List<Banco> listaBanco() {
        try {
            Query qry = getEntityManager().createNativeQuery(
                    " SELECT b.* \n "
                    + " FROM fin_banco b \n "
                    + "ORDER BY b.ds_banco", Banco.class
            );

            return qry.getResultList();
        } catch (Exception e) {
            e.getMessage();
        }
        return new ArrayList();
    }

    public Movimento pesquisaChequeEmitido(Integer id_pessoa, Integer id_banco, String agencia, String conta, String documento) {
        try {
            Query qry = getEntityManager().createNativeQuery(
                    " SELECT m.* \n "
                    + "  FROM fin_movimento m \n "
                    + " WHERE m.id_pessoa = " + id_pessoa + " \n "
                    + "   AND m.id_banco = " + id_banco + " \n "
                    + "   AND m.ds_agencia = '" + agencia + "' \n "
                    + "   AND m.ds_conta = '" + conta + "' \n "
                    + "   AND m.ds_documento = '" + documento + "'", Movimento.class
            );

            return (Movimento) qry.getSingleResult();
        } catch (Exception e) {
            e.getMessage();
        }
        return null;
    }

    public Emitente pesquisaEmitente(String documento) {
        try {
            Query qry = getEntityManager().createNativeQuery(
                    " SELECT e.* \n "
                    + "  FROM pes_emitente e \n "
                    + " WHERE e.ds_documento LIKE '" + documento + "'", Emitente.class
            );

            return (Emitente) qry.getSingleResult();
        } catch (Exception e) {
            e.getMessage();
        }
        return new Emitente();
    }

    public List<Object[]> listaMovimentosEmitente(Integer id_emitente) {
        try {
            Query qry = getEntityManager().createNativeQuery(
                    " SELECT COUNT(*) AS quantidade, 'Vencidos' AS status, sum(m.nr_valor) AS valor_total, 1 AS order_by \n "
                    + "  FROM fin_movimento m \n "
                    + " WHERE m.id_emitente IS NOT NULL \n "
                    + "   AND m.id_emitente = " + id_emitente + " \n "
                    + "   AND m.dt_vencimento < CURRENT_DATE \n "
                    + " GROUP BY m.id_emitente \n "
                    + " UNION \n "
                    + "SELECT COUNT(*) AS quantidade, 'A Vencer' AS status, sum(m.nr_valor) AS valor_total, 2 AS order_by \n "
                    + "  FROM fin_movimento m \n "
                    + " WHERE m.id_emitente IS NOT NULL \n "
                    + "   AND m.id_emitente = " + id_emitente + " \n "
                    + "   AND m.dt_vencimento >= CURRENT_DATE \n "
                    + " GROUP BY m.id_emitente \n "
                    + " UNION \n "
                    + "SELECT COUNT(*) AS quantidade, 'Total' AS status, SUM(m.nr_valor) AS valor_total, 3 AS order_by \n "
                    + "  FROM fin_movimento m \n "
                    + " WHERE m.id_emitente IS NOT NULL \n "
                    + "   AND m.id_emitente = " + id_emitente + " \n "
                    + " GROUP BY m.id_emitente \n "
                    + " ORDER BY 4"
            );

            return qry.getResultList();
        } catch (Exception e) {
            e.getMessage();
        }
        return new ArrayList();
    }

}
