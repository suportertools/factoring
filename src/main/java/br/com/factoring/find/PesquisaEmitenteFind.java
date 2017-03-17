/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.factoring.find;

import br.com.factoring.dao.PessoaDao;
import br.com.factoring.model.Emitente;
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
public class PesquisaEmitenteFind {

    private String nome;
    private List<Emitente> listaEmitente;

    public PesquisaEmitenteFind() {
        this.nome = "";
        this.listaEmitente = new ArrayList();
    }

    public PesquisaEmitenteFind(String nome, List<Emitente> listaEmitente) {
        this.nome = nome;
        this.listaEmitente = listaEmitente;
    }

    public void pesquisar() {
        nome = "";
        loadListaEmitente();
        PF.update("form_pesquisa_emitente");
        PF.openDialog("dlg_pesquisar_emitente");
    }

    public void loadListaEmitente() {
        Sessao.remove("emitenteToReturn");
        listaEmitente.clear();
        listaEmitente = new PessoaDao().listaEmitente(PesquisaEmitenteFind.this);
    }

    public void selecionar(Emitente e) {
        Sessao.put("emitenteToReturn", e);
    }

    public List<Emitente> getListaEmitente() {
        return listaEmitente;
    }

    public void setListaEmitente(List<Emitente> listaEmitente) {
        this.listaEmitente = listaEmitente;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

}
