/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.factoring.model;

import br.com.factoring.utils.Datas;
import br.com.factoring.utils.Moeda;
import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 *
 * @author Claudemir Rtools
 */
@Entity
@Table(name = "fin_movimento")
public class Movimento implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;
    @Temporal(TemporalType.DATE)
    @Column(name = "dt_lancamento")
    private Date lancamento;
    @Temporal(TemporalType.DATE)
    @Column(name = "dt_vencimento")
    private Date vencimento;
    @Temporal(TemporalType.DATE)
    @Column(name = "dt_vencimento_original")
    private Date vencimentoOriginal;
    @JoinColumn(name = "id_pessoa", referencedColumnName = "id", nullable = false)
    @ManyToOne
    private Pessoa pessoa;
    @JoinColumn(name = "id_tipo_documento", referencedColumnName = "id", nullable = false)
    @ManyToOne
    private FTipoDocumento tipoDocumento;
    @Column(name = "ds_documento", length = 30)
    private String documento;
    @JoinColumn(name = "id_banco", referencedColumnName = "id")
    @ManyToOne
    private Banco banco;
    @Column(name = "ds_agencia", length = 10)
    private String agencia;
    @Column(name = "ds_conta", length = 10)
    private String conta;
    @JoinColumn(name = "id_praca", referencedColumnName = "id")
    @ManyToOne
    private Cidade praca;
    @JoinColumn(name = "id_emitente", referencedColumnName = "id")
    @ManyToOne
    private Emitente emitente;
    @Column(name = "nr_valor")
    private Float valor;
    @Column(name = "ds_observacao", length = 300)
    private String observacao;

    public Movimento() {
        this.id = -1;
        this.lancamento = Datas.dataHoje();
        this.vencimento = null;
        this.vencimentoOriginal = null;
        this.pessoa = new Pessoa();
        this.tipoDocumento = new FTipoDocumento();
        this.documento = "";
        this.banco = new Banco();
        this.agencia = "";
        this.conta = "";
        this.praca = new Cidade();
        this.emitente = new Emitente();
        this.valor = new Float(0);
        this.observacao = "";
    }

    public Movimento(int id, Date lancamento, Date vencimento, Date vencimentoOriginal, Pessoa pessoa, FTipoDocumento tipoDocumento, String documento, Banco banco, String agencia, String conta, Cidade praca, Emitente emitente, Float valor, String observacao) {
        this.id = id;
        this.lancamento = lancamento;
        this.vencimento = vencimento;
        this.vencimentoOriginal = vencimentoOriginal;
        this.pessoa = pessoa;
        this.tipoDocumento = tipoDocumento;
        this.documento = documento;
        this.banco = banco;
        this.agencia = agencia;
        this.conta = conta;
        this.praca = praca;
        this.emitente = emitente;
        this.valor = valor;
        this.observacao = observacao;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getLancamento() {
        return lancamento;
    }

    public void setLancamento(Date lancamento) {
        this.lancamento = lancamento;
    }

    public String getLancamentoString() {
        return Datas.converteData(lancamento);
    }

    public void setLancamentoString(String lancamentoString) {
        this.lancamento = Datas.converte(lancamentoString);
    }

    public Date getVencimento() {
        return vencimento;
    }

    public void setVencimento(Date vencimento) {
        this.vencimento = vencimento;
    }

    public String getVencimentoString() {
        return Datas.converteData(vencimento);
    }

    public void setVencimentoString(String vencimentoString) {
        this.vencimento = Datas.converte(vencimentoString);
    }

    public Date getVencimentoOriginal() {
        return vencimentoOriginal;
    }

    public void setVencimentoOriginal(Date vencimentoOriginal) {
        this.vencimentoOriginal = vencimentoOriginal;
    }

    public String getVencimentoOriginalString() {
        return Datas.converteData(vencimentoOriginal);
    }

    public void setVencimentoOriginalString(String vencimentoOriginalString) {
        this.vencimentoOriginal = Datas.converte(vencimentoOriginalString);
    }

    public Pessoa getPessoa() {
        return pessoa;
    }

    public void setPessoa(Pessoa pessoa) {
        this.pessoa = pessoa;
    }

    public FTipoDocumento getTipoDocumento() {
        return tipoDocumento;
    }

    public void setTipoDocumento(FTipoDocumento tipoDocumento) {
        this.tipoDocumento = tipoDocumento;
    }

    public String getDocumento() {
        return documento;
    }

    public void setDocumento(String documento) {
        this.documento = documento;
    }

    public Banco getBanco() {
        return banco;
    }

    public void setBanco(Banco banco) {
        this.banco = banco;
    }

    public String getAgencia() {
        return agencia;
    }

    public void setAgencia(String agencia) {
        this.agencia = agencia;
    }

    public String getConta() {
        return conta;
    }

    public void setConta(String conta) {
        this.conta = conta;
    }

    public Cidade getPraca() {
        return praca;
    }

    public void setPraca(Cidade praca) {
        this.praca = praca;
    }

    public Emitente getEmitente() {
        return emitente;
    }

    public void setEmitente(Emitente emitente) {
        this.emitente = emitente;
    }

    public Float getValor() {
        return valor;
    }

    public void setValor(Float valor) {
        this.valor = valor;
    }

    public String getValorString() {
        return Moeda.converteR$Float(valor);
    }

    public void setValorString(String valorString) {
        this.valor = Moeda.converteUS$(valorString);
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

}
