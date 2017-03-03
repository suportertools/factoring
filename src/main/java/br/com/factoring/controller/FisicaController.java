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
import br.com.factoring.model.Endereco;
import br.com.factoring.model.Fisica;
import br.com.factoring.model.Juridica;
import br.com.factoring.model.TipoDocumento;
import br.com.factoring.model.TipoEndereco;
import br.com.factoring.utils.CEPService;
import br.com.factoring.utils.MensagemFlash;
import br.com.factoring.utils.PF;
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
public class FisicaController {

    private Fisica fisica = new Fisica();
    private List<Fisica> listaFisica = new ArrayList();
    private List<Endereco> listaEndereco = new ArrayList();

    private PesquisaEmpresa pesquisaEmpresa = new PesquisaEmpresa();
    private PesquisaFisica pesquisaFisica = new PesquisaFisica();
    private PesquisaEndereco pesquisaEndereco = new PesquisaEndereco();

    public void loadPagina() {
        if (!FacesContext.getCurrentInstance().isPostback()) {
            pesquisaFisica.loadListaFisica();
        }
    }

    public void salvarEndereco() {
        if (pesquisaEndereco.getEndereco().getNumero().isEmpty()) {
            MensagemFlash.fatal("Atenção", "DIGITE UM NÚMERO PARA O ENDEREÇO!");
            return;
        }

        Dao dao = new Dao();

        pesquisaEndereco.endereco.setPessoa(fisica.getPessoa());

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
        PF.update("form_fisica");
    }

    public void excluirEndereco(Endereco e) {
        if (e != null){
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

        dao.begin();

        fisica.getPessoa().setTipoDocumento((TipoDocumento) dao.find(new TipoDocumento(), 1));

        if (fisica.getId() == -1) {
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

    public PesquisaEndereco getPesquisaEndereco() {
        return pesquisaEndereco;
    }

    public void setPesquisaEndereco(PesquisaEndereco pesquisaEndereco) {
        this.pesquisaEndereco = pesquisaEndereco;
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
