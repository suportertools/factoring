/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.factoring.find;

import br.com.factoring.dao.PessoaDao;
import br.com.factoring.model.Pessoa;
import br.com.factoring.utils.PF;
import br.com.factoring.utils.Sessao;
import java.util.ArrayList;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

/**
 *
 * @author Claudemir Rtools
 */
@ManagedBean
@SessionScoped
public class PesquisaPessoaFind {

    private String descricao;
    private List<Pessoa> listaPessoa;
    private String porPesquisa;
    private String maskDocumento;

    public PesquisaPessoaFind() {
        this.descricao = "";
        this.listaPessoa = new ArrayList();
        this.porPesquisa = "";
        this.maskDocumento = "";
    }

    public PesquisaPessoaFind(String descricao, List<Pessoa> listaPessoa, String porPesquisa, String maskDocumento) {
        this.descricao = descricao;
        this.listaPessoa = listaPessoa;
        this.porPesquisa = porPesquisa;
        this.maskDocumento = maskDocumento;
    }

    public void pesquisar() {
        descricao = "";
        loadListaPessoa();
        PF.update("form_pesquisa_pessoa");
        PF.openDialog("dlg_pesquisar_pessoa");
    }

    public void alteraPorPesquisa() {
        this.descricao = "";
        switch (porPesquisa) {
            case "nome":
                maskDocumento = "";
                break;
            case "cpf":
                maskDocumento = "999.999.999-99";
                break;
            case "cnpj":
                maskDocumento = "99.999.999/9999-99";
                break;
        }
    }

    public void loadListaPessoa() {
        Sessao.remove("pessoaToReturn");
        listaPessoa.clear();
        listaPessoa = new PessoaDao().listaPessoa(PesquisaPessoaFind.this);
    }

    public void selecionar(Pessoa p) {
        Sessao.put("pessoaToReturn", p);
    }

    public List<Pessoa> getListaPessoa() {
        return listaPessoa;
    }

    public void setListaPessoa(List<Pessoa> listaPessoa) {
        this.listaPessoa = listaPessoa;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getPorPesquisa() {
        return porPesquisa;
    }

    public void setPorPesquisa(String porPesquisa) {
        this.porPesquisa = porPesquisa;
    }

    public String getMaskDocumento() {
        return maskDocumento;
    }

    public void setMaskDocumento(String maskDocumento) {
        this.maskDocumento = maskDocumento;
    }

}
