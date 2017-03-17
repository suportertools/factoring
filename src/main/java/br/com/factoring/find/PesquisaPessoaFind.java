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

    private String nome;
    private List<Pessoa> listaPessoa;

    public PesquisaPessoaFind() {
        this.nome = "";
        this.listaPessoa = new ArrayList();
    }

    public PesquisaPessoaFind(String nome, List<Pessoa> listaPessoa) {
        this.nome = nome;
        this.listaPessoa = listaPessoa;
    }

    public void pesquisar() {
        nome = "";
        loadListaPessoa();
        PF.update("form_pesquisa_pessoa");
        PF.openDialog("dlg_pesquisar_pessoa");
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

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

}
