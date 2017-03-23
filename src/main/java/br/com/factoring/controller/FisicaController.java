/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.factoring.controller;

import br.com.factoring.connections.Dao;
import br.com.factoring.dao.EnderecoDao;
import br.com.factoring.dao.FisicaDao;
import br.com.factoring.dao.JuridicaDao;
import br.com.factoring.dao.PessoaDao;
import br.com.factoring.model.Endereco;
import br.com.factoring.model.Fisica;
import br.com.factoring.model.Juridica;
import br.com.factoring.model.TipoDocumento;
import br.com.factoring.utils.Datas;
import br.com.factoring.utils.MensagemFlash;
import br.com.factoring.utils.PF;
import br.com.factoring.utils.Redirectx;
import br.com.factoring.utils.Sessao;
import br.com.factoring.utils.ValidaDocumento;
import java.util.ArrayList;
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
public class FisicaController {

    private Fisica fisica = new Fisica();
    private List<Fisica> listaFisica = new ArrayList();
    private List<Endereco> listaEndereco = new ArrayList();

    private PesquisaEmpresa pesquisaEmpresa = new PesquisaEmpresa();
    private PesquisaFisica pesquisaFisica = new PesquisaFisica();

    public void loadFisica() {
        if (!FacesContext.getCurrentInstance().isPostback()) {

            if (!new PermissaoController().temPermissao("cadastro_fisica")) {
                Redirectx.go("dashboard");
            }
        }
    }

    public void loadPagina() {
        if (!FacesContext.getCurrentInstance().isPostback()) {
            pesquisaFisica.loadListaFisica();

            if (!new PermissaoController().temPermissao("lista_fisica")) {
                Redirectx.go("dashboard");
            }
        }
    }

    public void removerEmpresa() {
        fisica.setJuridica(null);
    }

    public void loadListaEndereco() {
        listaEndereco.clear();
        listaEndereco = new EnderecoDao().listaEndereco(fisica.getPessoa().getId());
    }

    public void salvar() {
        Dao dao = new Dao();

        if (fisica.getPessoa().getNome().length() < 5) {
            MensagemFlash.fatal("Atenção", "DIGITE UM NOME VÁLIDO!");
            return;
        }

        if (!fisica.getNascimentoString().isEmpty()) {
            if (Datas.converteDataParaInteger(fisica.getNascimentoString()) > Datas.converteDataParaInteger(Datas.data())) {
                MensagemFlash.fatal("Atenção", "IDADE DA PESSOA, DEVE SER MENOR QUE DATA DE HOJE!");
                return;
            }

            if (Datas.calcularIdade(fisica.getNascimentoString()) > 150) {
                MensagemFlash.fatal("Atenção", "IDADE DA PESSOA NÃO PODE SER MAIOR QUE 150 ANOS!");
                return;
            }
        }

        if (!ValidaDocumento.isValidoCPF(fisica.getPessoa().getDocumento())) {
            MensagemFlash.fatal("Atenção", "DIGITE CPF VÁLIDO!");
            return;
        }

        fisica.getPessoa().setTipoDocumento((TipoDocumento) dao.find(new TipoDocumento(), 1));

        dao.begin();
        if (fisica.getId() == -1) {
            if (new PessoaDao().pesquisaPessoaDocumento(null, fisica.getPessoa().getDocumento()) != null) {
                dao.rollback();
                MensagemFlash.fatal("Atenção", "ESTE DOCUMENTO JÁ ESTA CADASTRADO!");
                return;
            }

            if (!dao.save(fisica.getPessoa())) {
                dao.rollback();
                MensagemFlash.fatal("Atenção", "ERRO AO SALVAR PESSOA!");
                return;
            }

            if (!dao.save(fisica)) {
                dao.rollback();
                MensagemFlash.fatal("Atenção", "ERRO AO SALVAR FÍSICA!");
                return;
            }
        } else {
            if (new PessoaDao().pesquisaPessoaDocumento(fisica.getPessoa().getId(), fisica.getPessoa().getDocumento()) != null) {
                dao.rollback();
                MensagemFlash.fatal("Atenção", "ESTE DOCUMENTO JÁ ESTA CADASTRADO!");
                return;
            }

            if (!dao.update(fisica.getPessoa())) {
                dao.rollback();
                MensagemFlash.fatal("Atenção", "ERRO AO ATUALIZAR PESSOA!");
                return;
            }

            if (!dao.update(fisica)) {
                dao.rollback();
                MensagemFlash.fatal("Atenção", "ERRO AO ATUALIZAR FÍSICA!");
                return;
            }
        }

        dao.commit();
        MensagemFlash.info("Sucesso", "PESSOA FÍSICA SALVA!");
    }

    public void novo() {
        fisica = new Fisica();
        loadListaEndereco();
    }

    public String editar(Fisica f) {
        fisica = f;
        loadListaEndereco();
        return "fisica";
    }

    public void excluir() {
        Dao dao = new Dao();

        dao.begin();

        if (!listaEndereco.isEmpty()) {
            for (int i = 0; i < listaEndereco.size(); i++) {
                if (!dao.remove(listaEndereco.get(i))) {
                    dao.rollback();
                    MensagemFlash.fatal("Atenção", "ERRO AO EXCLUIR ENDEREÇO!");
                    return;
                }
            }
        }

        if (!dao.remove(fisica)) {
            dao.rollback();
            MensagemFlash.fatal("Atenção", "ERRO AO EXCLUIR FÍSICA!");
            return;
        }

        if (!dao.remove(fisica.getPessoa())) {
            dao.rollback();
            MensagemFlash.fatal("Atenção", "ERRO AO EXCLUIR PESSOA!");
            return;
        }

        dao.commit();

        fisica = new Fisica();
        loadListaEndereco();

        MensagemFlash.info("Sucesso", "PESSOA FÍSICA EXCLUÍDA!");
    }

    public String paginaCadastrarFisica() {
        fisica = new Fisica();
        loadListaEndereco();
        return "fisica";
    }

    public Fisica getFisica() {
        return fisica;
    }

    public void setFisica(Fisica fisica) {
        this.fisica = fisica;
    }

    public List<Fisica> getListaFisica() {
        return listaFisica;
    }

    public void setListaFisica(List<Fisica> listaFisica) {
        this.listaFisica = listaFisica;
    }

    public List<Endereco> getListaEndereco() {
        if (Sessao.exist("enderecoToReturn")) {
            Sessao.remove("enderecoToReturn");
            loadListaEndereco();
        }
        return listaEndereco;
    }

    public void setListaEndereco(List<Endereco> listaEndereco) {
        this.listaEndereco = listaEndereco;
    }

    public PesquisaEmpresa getPesquisaEmpresa() {
        return pesquisaEmpresa;
    }

    public void setPesquisaEmpresa(PesquisaEmpresa pesquisaEmpresa) {
        this.pesquisaEmpresa = pesquisaEmpresa;
    }

    public PesquisaFisica getPesquisaFisica() {
        return pesquisaFisica;
    }

    public void setPesquisaFisica(PesquisaFisica pesquisaFisica) {
        this.pesquisaFisica = pesquisaFisica;
    }

    public class PesquisaFisica {

        private String nome;
        private List<Fisica> listaFisica = new ArrayList();

        public void novo() {
            listaFisica.clear();
            nome = "";
        }

        public void loadListaFisica() {
            listaFisica.clear();
            listaFisica = new FisicaDao().listaPesquisaFisica(PesquisaFisica.this);
        }

        public PesquisaFisica() {
            this.nome = "";
        }

        public PesquisaFisica(String nome) {
            this.nome = nome;
        }

        public String getNome() {
            return nome;
        }

        public void setNome(String nome) {
            this.nome = nome;
        }

        public List<Fisica> getListaFisica() {
            return listaFisica;
        }

        public void setListaFisica(List<Fisica> listaFisica) {
            this.listaFisica = listaFisica;
        }
    }

    public class PesquisaEmpresa {

        private String nome;
        private Juridica empresa = new Juridica();
        private List<Juridica> listaEmpresa = new ArrayList();

        public void novo() {
            listaEmpresa.clear();
            nome = "";
        }

        public void loadListaEmpresa() {
            listaEmpresa.clear();
            listaEmpresa = new JuridicaDao().listaPesquisaEmpresa(PesquisaEmpresa.this);
        }

        public void selecionar(Juridica j) {
            empresa = j;
            fisica.setJuridica(j);
        }

        public PesquisaEmpresa() {
            this.nome = "";
        }

        public PesquisaEmpresa(String nome) {
            this.nome = nome;
        }

        public String getNome() {
            return nome;
        }

        public void setNome(String nome) {
            this.nome = nome;
        }

        public List<Juridica> getListaEmpresa() {
            return listaEmpresa;
        }

        public void setListaEmpresa(List<Juridica> listaEmpresa) {
            this.listaEmpresa = listaEmpresa;
        }

        public Juridica getEmpresa() {
            return empresa;
        }

        public void setEmpresa(Juridica empresa) {
            this.empresa = empresa;
        }
    }
}
