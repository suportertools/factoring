/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.factoring.controller;

import br.com.factoring.connections.Dao;
import br.com.factoring.dao.EnderecoDao;
import br.com.factoring.dao.JuridicaDao;
import br.com.factoring.dao.PessoaDao;
import br.com.factoring.model.Endereco;
import br.com.factoring.model.Juridica;
import br.com.factoring.model.TipoDocumento;
import br.com.factoring.utils.MensagemFlash;
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
public class JuridicaController {

    private Juridica juridica = new Juridica();

    private List<Endereco> listaEndereco = new ArrayList();

    private PesquisaJuridica pesquisaJuridica = new PesquisaJuridica();

    public void loadJuridica() {
        if (!FacesContext.getCurrentInstance().isPostback()) {
            if (!new PermissaoController().temPermissao("cadastro_juridica")) {
                Redirectx.go("dashboard");
            }
        }
    }

    public void loadPagina() {
        if (!FacesContext.getCurrentInstance().isPostback()) {
            pesquisaJuridica.novo();

            if (!new PermissaoController().temPermissao("lista_juridica")) {
                Redirectx.go("dashboard");
            }
        }
    }

    public void loadListaEndereco() {
        this.listaEndereco = new EnderecoDao().listaEndereco(juridica.getPessoa().getId());
    }

    public void salvar() {
        Dao dao = new Dao();

        if (juridica.getPessoa().getNome().length() < 5) {
            MensagemFlash.fatal("Atenção", "DIGITE UM NOME VÁLIDO!");
            return;
        }

        if (!ValidaDocumento.isValidoCNPJ(juridica.getPessoa().getDocumento())) {
            MensagemFlash.fatal("Atenção", "DIGITE CNPJ VÁLIDO!");
            return;
        }

        juridica.getPessoa().setTipoDocumento((TipoDocumento) dao.find(new TipoDocumento(), 2));

        dao.begin();
        if (juridica.getId() == -1) {
            if (new PessoaDao().pesquisaPessoaDocumento(null, juridica.getPessoa().getDocumento()) != null) {
                dao.rollback();
                MensagemFlash.fatal("Atenção", "ESTE DOCUMENTO JÁ ESTA CADASTRADO!");
                return;
            }

            if (!dao.save(juridica.getPessoa())) {
                dao.rollback();
                MensagemFlash.fatal("Atenção", "ERRO AO SALVAR PESSOA!");
                return;
            }

            if (!dao.save(juridica)) {
                dao.rollback();
                MensagemFlash.fatal("Atenção", "ERRO AO SALVAR JURÍDICA!");
                return;
            }
        } else {
            if (new PessoaDao().pesquisaPessoaDocumento(juridica.getPessoa().getId(), juridica.getPessoa().getDocumento()) != null) {
                dao.rollback();
                MensagemFlash.fatal("Atenção", "ESTE DOCUMENTO JÁ ESTA CADASTRADO!");
                return;
            }

            if (!dao.update(juridica.getPessoa())) {
                dao.rollback();
                MensagemFlash.fatal("Atenção", "ERRO AO ATUALIZAR PESSOA!");
                return;
            }

            if (!dao.update(juridica)) {
                dao.rollback();
                MensagemFlash.fatal("Atenção", "ERRO AO ATUALIZAR JURÍDICA!");
                return;
            }
        }

        dao.commit();
        MensagemFlash.info("Sucesso", "PESSOA JURÍDICA SALVA!");
    }

    public void novo() {
        juridica = new Juridica();
        loadListaEndereco();
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

        if (!dao.remove(juridica)) {
            dao.rollback();
            MensagemFlash.fatal("Atenção", "ERRO AO EXCLUIR JURÍDICA!");
            return;
        }

        if (!dao.remove(juridica.getPessoa())) {
            dao.rollback();
            MensagemFlash.fatal("Atenção", "ERRO AO EXCLUIR PESSOA!");
            return;
        }

        dao.commit();

        juridica = new Juridica();
        loadListaEndereco();

        MensagemFlash.info("Sucesso", "PESSOA JURÍDICA EXCLUÍDA!");
    }

    public String editar(Juridica j) {
        juridica = j;
        loadListaEndereco();
        return "juridica";
    }

    public String paginaCadastrarJuridica() {
        juridica = new Juridica();
        return "juridica";
    }

    public Juridica getJuridica() {
        return juridica;
    }

    public void setJuridica(Juridica juridica) {
        this.juridica = juridica;
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

    public PesquisaJuridica getPesquisaJuridica() {
        return pesquisaJuridica;
    }

    public void setPesquisaJuridica(PesquisaJuridica pesquisaJuridica) {
        this.pesquisaJuridica = pesquisaJuridica;
    }

    public class PesquisaJuridica {

        private String descricao = "";
        private List<Juridica> listaJuridica = new ArrayList();
        private String porPesquisa;
        private String maskDocumento;

        public void novo() {
            this.listaJuridica.clear();
            this.descricao = "";
            this.porPesquisa = "nome";
            this.maskDocumento = "";
        }

        public void loadListaJuridica() {
            try {
                this.listaJuridica.clear();
                this.listaJuridica = new JuridicaDao().listaPesquisaJuridica(PesquisaJuridica.this);
            } catch (Exception e) {
                e.getMessage();
            }
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

        public PesquisaJuridica() {
            this.descricao = "";
        }

        public PesquisaJuridica(String descricao) {
            this.descricao = descricao;
        }

        public String getDescricao() {
            return descricao;
        }

        public void setDescricao(String descricao) {
            this.descricao = descricao;
        }

        public List<Juridica> getListaJuridica() {
            return listaJuridica;
        }

        public void setListaJuridica(List<Juridica> listaJuridica) {
            this.listaJuridica = listaJuridica;
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
}
