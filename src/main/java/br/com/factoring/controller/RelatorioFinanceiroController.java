/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.factoring.controller;

import br.com.factoring.dao.RelatorioFinanceiroDao;
import br.com.factoring.find.PesquisaPessoaFind;
import br.com.factoring.model.Emitente;
import br.com.factoring.model.Pessoa;
import br.com.factoring.utils.Jasper;
import br.com.factoring.utils.Redirectx;
import br.com.factoring.utils.Sessao;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

/**
 *
 * @author Claudemir Rtools
 */
@ManagedBean
@SessionScoped
public class RelatorioFinanceiroController implements Serializable {

    private FiltroRelatorioFinanceiro filtroRelatorioFinanceiro = new FiltroRelatorioFinanceiro();

    public void loadPagina() {
        if (!FacesContext.getCurrentInstance().isPostback()) {
            
            if (!new PermissaoController().temPermissao("relatorio_financeiro")) {
                Redirectx.go("dashboard");
            }
        }
    }
    
    public String paginaRelatorioFinanceiro() {
        Sessao.put("relatorioFinanceiroController", new RelatorioFinanceiroController());
        return "relatorio_financeiro";
    }

    public void imprimir() {
        RelatorioFinanceiroDao dao = new RelatorioFinanceiroDao();

        List<Object> result = dao.listaRelatorioFinanceiro(filtroRelatorioFinanceiro);
        List<ObjectRelatorio> lista = new ArrayList();

        for (Object result1 : result) {
            Object[] linha = (Object[]) result1;

            lista.add(new ObjectRelatorio(
                    linha[0],
                    linha[1],
                    linha[2],
                    linha[3],
                    linha[4],
                    linha[5],
                    linha[6],
                    linha[7],
                    linha[8],
                    linha[9],
                    linha[10],
                    linha[11],
                    linha[12])
            );
        }

        Jasper.printReports(filtroRelatorioFinanceiro.getTipoRelatorio().toUpperCase(), filtroRelatorioFinanceiro.getTipoRelatorio().toUpperCase().replace("_", " "), lista, new HashMap());
    }

    public void limparFiltro() {
        filtroRelatorioFinanceiro = new FiltroRelatorioFinanceiro();
    }

    public FiltroRelatorioFinanceiro getFiltroRelatorioFinanceiro() {
        if (Sessao.exist("pessoaToReturn")) {
            filtroRelatorioFinanceiro.setCliente((Pessoa) Sessao.get("pessoaToReturn", true));
        }

        if (Sessao.exist("emitenteToReturn")) {
            filtroRelatorioFinanceiro.setEmitente((Emitente) Sessao.get("emitenteToReturn", true));
        }
        return filtroRelatorioFinanceiro;
    }

    public void setFiltroRelatorioFinanceiro(FiltroRelatorioFinanceiro filtroRelatorioFinanceiro) {
        this.filtroRelatorioFinanceiro = filtroRelatorioFinanceiro;
    }

    public class FiltroRelatorioFinanceiro {

        private Boolean chkCliente;
        private Boolean chkEmitente;
        private Boolean chkTipoData;

        private Pessoa cliente;
        private Emitente emitente;
        private String tipoData;
        private String dataInicial;
        private String dataFinal;

        private String tipoRelatorio;

        public FiltroRelatorioFinanceiro() {
            this.chkCliente = false;
            this.chkEmitente = false;
            this.chkTipoData = false;
            this.cliente = new Pessoa();
            this.emitente = new Emitente();
            this.tipoData = "a_vencer";
            this.dataInicial = "";
            this.dataFinal = "";
            this.tipoRelatorio = "";
        }

        public FiltroRelatorioFinanceiro(Boolean chkCliente, Boolean chkEmitente, Boolean chkTipoData, Pessoa cliente, Emitente emitente, String tipoData, String dataInicial, String dataFinal, String tipoRelatorio) {
            this.chkCliente = chkCliente;
            this.chkEmitente = chkEmitente;
            this.chkTipoData = chkTipoData;
            this.cliente = cliente;
            this.emitente = emitente;
            this.tipoData = tipoData;
            this.dataInicial = dataInicial;
            this.dataFinal = dataFinal;
            this.tipoRelatorio = tipoRelatorio;
        }

        public void limpar(String chk) {
            switch (chk) {
                case "chk_cliente":
                    cliente = new Pessoa();
                    break;
                case "chk_emitente":
                    emitente = new Emitente();
                    break;
                case "chk_data":
                    tipoData = "a_vencer";
                    dataInicial = "";
                    dataFinal = "";
                    break;
                default:
                    break;
            }
        }

        public Boolean getChkCliente() {
            return chkCliente;
        }

        public void setChkCliente(Boolean chkCliente) {
            this.chkCliente = chkCliente;
        }

        public Boolean getChkEmitente() {
            return chkEmitente;
        }

        public void setChkEmitente(Boolean chkEmitente) {
            this.chkEmitente = chkEmitente;
        }

        public Boolean getChkTipoData() {
            return chkTipoData;
        }

        public void setChkTipoData(Boolean chkTipoData) {
            this.chkTipoData = chkTipoData;
        }

        public Pessoa getCliente() {
            return cliente;
        }

        public void setCliente(Pessoa cliente) {
            this.cliente = cliente;
        }

        public Emitente getEmitente() {
            return emitente;
        }

        public void setEmitente(Emitente emitente) {
            this.emitente = emitente;
        }

        public String getTipoData() {
            return tipoData;
        }

        public void setTipoData(String tipoData) {
            this.tipoData = tipoData;
        }

        public String getDataInicial() {
            return dataInicial;
        }

        public void setDataInicial(String dataInicial) {
            this.dataInicial = dataInicial;
        }

        public String getDataFinal() {
            return dataFinal;
        }

        public void setDataFinal(String dataFinal) {
            this.dataFinal = dataFinal;
        }

        public String getTipoRelatorio() {
            return tipoRelatorio;
        }

        public void setTipoRelatorio(String tipoRelatorio) {
            this.tipoRelatorio = tipoRelatorio;
        }
    }

    public class ObjectRelatorio {

        private Object lancamento;
        private Object pessoa_documento;
        private Object pessoa_nome;
        private Object banco;
        private Object agencia;
        private Object conta;
        private Object documento;
        private Object praca;
        private Object valor;
        private Object vencimento;
        private Object emitente;
        private Object pessoa_id;
        private Object emitente_id;

        public ObjectRelatorio(Object lancamento, Object pessoa_documento, Object pessoa_nome, Object banco, Object agencia, Object conta, Object documento, Object praca, Object valor, Object vencimento, Object emitente, Object pessoa_id, Object emitente_id) {
            this.lancamento = lancamento;
            this.pessoa_documento = pessoa_documento;
            this.pessoa_nome = pessoa_nome;
            this.banco = banco;
            this.agencia = agencia;
            this.conta = conta;
            this.documento = documento;
            this.praca = praca;
            this.valor = valor;
            this.vencimento = vencimento;
            this.emitente = emitente;
            this.pessoa_id = pessoa_id;
            this.emitente_id = emitente_id;
        }

        public Object getLancamento() {
            return lancamento;
        }

        public void setLancamento(Object lancamento) {
            this.lancamento = lancamento;
        }

        public Object getPessoa_documento() {
            return pessoa_documento;
        }

        public void setPessoa_documento(Object pessoa_documento) {
            this.pessoa_documento = pessoa_documento;
        }

        public Object getPessoa_nome() {
            return pessoa_nome;
        }

        public void setPessoa_nome(Object pessoa_nome) {
            this.pessoa_nome = pessoa_nome;
        }

        public Object getBanco() {
            return banco;
        }

        public void setBanco(Object banco) {
            this.banco = banco;
        }

        public Object getAgencia() {
            return agencia;
        }

        public void setAgencia(Object agencia) {
            this.agencia = agencia;
        }

        public Object getConta() {
            return conta;
        }

        public void setConta(Object conta) {
            this.conta = conta;
        }

        public Object getDocumento() {
            return documento;
        }

        public void setDocumento(Object documento) {
            this.documento = documento;
        }

        public Object getPraca() {
            return praca;
        }

        public void setPraca(Object praca) {
            this.praca = praca;
        }

        public Object getValor() {
            return valor;
        }

        public void setValor(Object valor) {
            this.valor = valor;
        }

        public Object getVencimento() {
            return vencimento;
        }

        public void setVencimento(Object vencimento) {
            this.vencimento = vencimento;
        }

        public Object getEmitente() {
            return emitente;
        }

        public void setEmitente(Object emitente) {
            this.emitente = emitente;
        }

        public Object getPessoa_id() {
            return pessoa_id;
        }

        public void setPessoa_id(Object pessoa_id) {
            this.pessoa_id = pessoa_id;
        }

        public Object getEmitente_id() {
            return emitente_id;
        }

        public void setEmitente_id(Object emitente_id) {
            this.emitente_id = emitente_id;
        }

    }

}
