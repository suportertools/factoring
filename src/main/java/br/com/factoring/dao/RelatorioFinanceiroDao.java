/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.factoring.dao;

import br.com.factoring.connections.DB;
import br.com.factoring.controller.RelatorioFinanceiroController;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Query;

/**
 *
 * @author Claudemir Rtools
 */
public class RelatorioFinanceiroDao extends DB {

    public List<Object> listaRelatorioFinanceiro(RelatorioFinanceiroController.FiltroRelatorioFinanceiro rf) {

        List<String> list_where = new ArrayList();

        String SELECT
                = "SELECT m.lancamento, \n"
                + "       m.pessoa_documento, \n "
                + "       m.nome, \n "
                + "       m.banco, \n "
                + "       m.agencia, \n "
                + "       m.conta, \n "
                + "       m.documento, \n "
                + "       m.praca, \n "
                + "       m.valor, \n "
                + "       m.vencimento, \n "
                + "       m.emitente, \n "
                + "       m.pessoa_id, \n "
                + "       m.emitente_id \n "
                + "  FROM movimento_vw m \n ";

        String GROUP = "", ORDER = "";

        if (rf.getChkCliente() && rf.getCliente().getId() != -1) {
            list_where.add("m.pessoa_id = " + rf.getCliente().getId());
        }

        if (rf.getChkEmitente() && rf.getEmitente().getId() != -1) {
            list_where.add("m.emitente_id = " + rf.getEmitente().getId());
        }

        if (rf.getChkTipoData()) {
            switch (rf.getTipoData()) {
                case "a_vencer":
                    list_where.add("m.vencimento >= CURRENT_DATE");
                    break;

                case "vencendo_hoje":
                    list_where.add("m.vencimento = CURRENT_DATE");
                    break;

                case "vencido_a_7_dias":
                    list_where.add("m.vencimento >= CURRENT_DATE AND m.vencimento <= CURRENT_DATE + 7");
                    break;

                case "faixa_vencimento":
                    if (!rf.getDataInicial().isEmpty() && rf.getDataFinal().isEmpty()) {
                        list_where.add("m.vencimento >= '" + rf.getDataInicial() + "'");
                    } else if (rf.getDataInicial().isEmpty() && !rf.getDataFinal().isEmpty()) {
                        list_where.add("m.vencimento <= '" + rf.getDataFinal() + "'");
                    } else if (rf.getDataInicial().isEmpty() && rf.getDataFinal().isEmpty()) {
                        list_where.add("m.vencimento = CURRENT_DATE AND m.vencimento = CURRENT_DATE");
                    } else {
                        list_where.add("m.vencimento >= '" + rf.getDataInicial() + "'" + " AND m.vencimento <= '" + rf.getDataFinal() + "'");
                    }
                    break;

                case "faixa_lancamento":
                    if (!rf.getDataInicial().isEmpty() && rf.getDataFinal().isEmpty()) {
                        list_where.add("m.lancamento >= '" + rf.getDataInicial() + "'");
                    } else if (rf.getDataInicial().isEmpty() && !rf.getDataFinal().isEmpty()) {
                        list_where.add("m.lancamento <= '" + rf.getDataFinal() + "'");
                    } else if (rf.getDataInicial().isEmpty() && rf.getDataFinal().isEmpty()) {
                        list_where.add("m.lancamento = CURRENT_DATE AND m.lancamento = CURRENT_DATE");
                    } else {
                        list_where.add("m.lancamento >= '" + rf.getDataInicial() + "'" + " AND m.lancamento <= '" + rf.getDataFinal() + "'");
                    }
                    break;
            }

        }

        String WHERE = "";
        for (String linha : list_where) {
            if (WHERE.isEmpty()) {
                WHERE = " WHERE " + linha + " \n ";
            } else {
                WHERE += " AND " + linha + " \n ";
            }
        }

        switch (rf.getTipoRelatorio()) {
            case "extrato_geral":
                GROUP = "";
                ORDER = " ORDER BY m.vencimento, m.lancamento, m.nome ";
                break;
            case "extrato_por_cliente":
                //GROUP = " GROUP BY m.pessoa_id, m.vencimento ";
                ORDER = " ORDER BY m.nome, m.vencimento ";
                break;
            case "extrato_por_emitente":
                //GROUP = " GROUP BY e.emitente_id, m.vencimento ";
                ORDER = " ORDER BY m.emitente, m.vencimento ";
                break;
            default:
                break;
        }

        try {
            Query qry = getEntityManager().createNativeQuery(
                    SELECT + WHERE + GROUP + ORDER
            );

            return qry.getResultList();
        } catch (Exception e) {
            e.getMessage();
        }
        return new ArrayList();
    }
}
