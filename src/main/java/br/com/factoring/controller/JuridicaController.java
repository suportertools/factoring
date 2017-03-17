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
import br.com.factoring.model.TipoEndereco;
import br.com.factoring.utils.CEPService;
import br.com.factoring.utils.MensagemFlash;
import br.com.factoring.utils.PF;
import br.com.factoring.utils.Redirectx;
import br.com.factoring.utils.ValidaDocumento;
import java.util.ArrayList;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

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
    private PesquisaEndereco pesquisaEndereco = new PesquisaEndereco();

    public void loadJuridica() {
        if (!FacesContext.getCurrentInstance().isPostback()) {
            if (!new PermissaoController().temPermissao("cadastro_juridica")) {
                Redirectx.go("dashboard");
            }
        }
    }
    
    public void loadPagina() {
        if (!FacesContext.getCurrentInstance().isPostback()) {
            pesquisaJuridica.loadListaJuridica();

            if (!new PermissaoController().temPermissao("lista_juridica")) {
                Redirectx.go("dashboard");
            }
        }
    }

    public void loadListaEndereco() {
        listaEndereco.clear();

        listaEndereco = new EnderecoDao().listaEndereco(juridica.getPessoa().getId());
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

    public void salvarEndereco() {
        if (pesquisaEndereco.getEndereco().getNumero().isEmpty()) {
            MensagemFlash.fatal("Atenção", "DIGITE UM NÚMERO PARA O ENDEREÇO!");
            return;
        }

        Dao dao = new Dao();

        pesquisaEndereco.endereco.setPessoa(juridica.getPessoa());

        dao.begin();
        if (pesquisaEndereco.endereco.getId() == -1) {
            if (!dao.save(pesquisaEndereco.endereco)) {
                dao.rollback();
                MensagemFlash.fatal("Erro", "NÃO FOI POSSÍVEL SALVAR O ENDEREÇO!");
                return;
            }
        } else if (!dao.update(pesquisaEndereco.endereco)) {
            dao.rollback();
            MensagemFlash.fatal("Erro", "NÃO FOI POSSÍVEL ALTERAR O ENDEREÇO!");
            return;
        }

        dao.commit();
        loadListaEndereco();
        MensagemFlash.info("Sucesso", "ENDEREÇO SALVO!");
        PF.closeDialog("dlg_pesquisa_endereco");
        PF.update("form_juridica");
    }

    public void excluirEndereco(Endereco e) {
        if (e != null) {
            pesquisaEndereco.endereco = e;
        }

        Dao dao = new Dao();

        dao.begin();

        if (!dao.remove(pesquisaEndereco.endereco)) {
            dao.rollback();
            MensagemFlash.error("Erro", "NÃO FOI POSSÍVEL EXCLUIR ENDEREÇO!");
            return;
        }

        dao.commit();

        pesquisaEndereco = new PesquisaEndereco();
        loadListaEndereco();

        MensagemFlash.info("Sucesso", "ENDEREÇO DELETADO!");
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

    public PesquisaEndereco getPesquisaEndereco() {
        return pesquisaEndereco;
    }

    public void setPesquisaEndereco(PesquisaEndereco pesquisaEndereco) {
        this.pesquisaEndereco = pesquisaEndereco;
    }

    public class PesquisaJuridica {

        private String nome = "";
        private List<Juridica> listaJuridica = new ArrayList();

        public void novo() {
            listaJuridica.clear();
            nome = "";
        }

        public void loadListaJuridica() {
            try {
                listaJuridica.clear();
                listaJuridica = new JuridicaDao().listaPesquisaJuridica(PesquisaJuridica.this);
            } catch (Exception e) {
                e.getMessage();
            }
        }

        public PesquisaJuridica() {
            this.nome = "";
        }

        public PesquisaJuridica(String nome) {
            this.nome = nome;
        }

        public String getNome() {
            return nome;
        }

        public void setNome(String nome) {
            this.nome = nome;
        }

        public List<Juridica> getListaJuridica() {
            return listaJuridica;
        }

        public void setListaJuridica(List<Juridica> listaJuridica) {
            this.listaJuridica = listaJuridica;
        }

    }

    public class PesquisaEndereco {

        private Endereco endereco;
        private List<Endereco> listaEndereco;

        private Integer indexTipoEndereco;
        private List<SelectItem> listaTipoEndereco;

        public PesquisaEndereco() {
            this.endereco = new Endereco();
            this.listaEndereco = new ArrayList();
            this.indexTipoEndereco = 0;
            this.listaTipoEndereco = new ArrayList();

            loadListaTipoEndereco();
            loadListaEndereco();
        }

        public PesquisaEndereco(Endereco endereco, List<Endereco> listaEndereco, Integer indexTipoEndereco, List<SelectItem> listaTipoEndereco) {
            this.endereco = endereco;
            this.listaEndereco = listaEndereco;
            this.indexTipoEndereco = indexTipoEndereco;
            this.listaTipoEndereco = listaTipoEndereco;
        }

        public void novo() {
            endereco = new Endereco();
            loadListaTipoEndereco();
            loadListaEndereco();
        }

        public final void loadListaEndereco() {
            listaEndereco.clear();
            if (!endereco.getCep().isEmpty()) {
                endereco = CEPService.procurar(endereco.getCep());
                if (endereco == null) {
                    endereco = new Endereco();
                    return;
                }
                selecionaTipoEndereco();
                listaEndereco.add(endereco);
            }
        }

        public void selecionaTipoEndereco() {
            endereco.setTipoEndereco((TipoEndereco) new Dao().find(new TipoEndereco(), Integer.valueOf(listaTipoEndereco.get(indexTipoEndereco).getDescription())));
        }

        public final void loadListaTipoEndereco() {
            listaTipoEndereco.clear();

            List<TipoEndereco> result = new EnderecoDao().listaTipoEndereco();

            for (int i = 0; i < result.size(); i++) {
                listaTipoEndereco.add(new SelectItem(
                        i, result.get(i).getDescricao(), Integer.toString(result.get(i).getId())
                ));
            }
        }

        public void selecionar(Endereco e) {
            endereco = e;
            for (int i = 0; i < listaTipoEndereco.size(); i++) {
                if (endereco.getTipoEndereco().getId() == Integer.valueOf(listaTipoEndereco.get(i).getDescription())) {
                    indexTipoEndereco = i;
                    break;
                }
            }
            //fisica.setJuridica(j);
        }

        public Endereco getEndereco() {
            return endereco;
        }

        public void setEndereco(Endereco endereco) {
            this.endereco = endereco;
        }

        public List<Endereco> getListaEndereco() {
            return listaEndereco;
        }

        public void setListaEndereco(List<Endereco> listaEndereco) {
            this.listaEndereco = listaEndereco;
        }

        public Integer getIndexTipoEndereco() {
            return indexTipoEndereco;
        }

        public void setIndexTipoEndereco(Integer indexTipoEndereco) {
            this.indexTipoEndereco = indexTipoEndereco;
        }

        public List<SelectItem> getListaTipoEndereco() {
            return listaTipoEndereco;
        }

        public void setListaTipoEndereco(List<SelectItem> listaTipoEndereco) {
            this.listaTipoEndereco = listaTipoEndereco;
        }

    }
}
