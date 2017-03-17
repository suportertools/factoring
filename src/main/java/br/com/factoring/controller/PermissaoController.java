/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.factoring.controller;

import br.com.factoring.connections.Dao;
import br.com.factoring.dao.PermissaoDao;
import br.com.factoring.model.Grupo;
import br.com.factoring.model.Pagina;
import br.com.factoring.model.Permissao;
import br.com.factoring.model.PermissaoGrupo;
import br.com.factoring.model.Usuario;
import br.com.factoring.utils.MensagemFlash;
import br.com.factoring.utils.Redirectx;
import br.com.factoring.utils.Sessao;
import java.io.Serializable;
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
public class PermissaoController implements Serializable {

    private List<SelectItem> listaGrupo = new ArrayList();
    private Integer indexListaGrupo = 0;

    private Grupo novoGrupo = new Grupo();
    private PesquisaPermissao pesquisaPermissao = new PesquisaPermissao();
    private List<ListaPagina> listaPagina = new ArrayList();

    public PermissaoController() {

    }

    public void loadPagina() {
        if (!FacesContext.getCurrentInstance().isPostback()) {
            loadListaGrupo();

            if (!temPermissao("cadastro_grupo")) {
                Redirectx.go("dashboard");
            }
        }
    }

    public void salvarGrupo() {
        Dao dao = new Dao();

        dao.begin();
        if (novoGrupo.getId() == -1) {
            if (!dao.save(novoGrupo)) {
                dao.rollback();
                MensagemFlash.error("Erro", "ERRO AO SALVAR NOVO GRUPO!");
                return;
            }
        } else if (!dao.update(novoGrupo)) {
            dao.rollback();
            MensagemFlash.error("Erro", "ERRO AO ATUALIZAR NOVO GRUPO!");
            return;
        }
        dao.commit();

        loadListaGrupo();
        MensagemFlash.info("Sucesso", "NOVO GRUPO ADICIONADO!");
    }

    public void editarGrupo() {
        novoGrupo = (Grupo) new Dao().find(new Grupo(), Integer.valueOf(listaGrupo.get(indexListaGrupo).getDescription()));
    }

    public void excluirGrupo() {
        Dao dao = new Dao();

        dao.begin();
        if (novoGrupo.getId() != -1) {
            if (!dao.remove(novoGrupo)) {
                dao.rollback();
                MensagemFlash.error("Erro", "ERRO AO EXCLUIR GRUPO!");
                return;
            }
        }
        dao.commit();

        loadListaGrupo();
        MensagemFlash.info("Sucesso", "GRUPO EXCLUÍDO!");
    }

    public final void loadListaPagina() {
        listaPagina.clear();

        List<Pagina> result = new PermissaoDao().listaPagina(pesquisaPermissao.descricao);
        Grupo l_grupo = (Grupo) new Dao().find(new Grupo(), Integer.valueOf(listaGrupo.get(indexListaGrupo).getDescription()));
        for (Pagina p : result) {
            List<Object[]> result_p = new PermissaoDao().listaPermissaoGrupo(l_grupo.getId(), p.getId());

            List<ListaPermissaoCheck> lpc = new ArrayList();

            for (Object[] l : result_p) {
                lpc.add(new ListaPermissaoCheck((Boolean) l[2], (Permissao) new Dao().find(new Permissao(), (Integer) l[0])));
            }

            if (!lpc.isEmpty()) {
                listaPagina.add(
                        new ListaPagina(
                                p,
                                lpc
                        )
                );
            }
        }
    }

    public void editarPermissaoPagina(ListaPermissaoCheck lpc) {
        Dao dao = new Dao();
        Grupo l_grupo = (Grupo) new Dao().find(new Grupo(), Integer.valueOf(listaGrupo.get(indexListaGrupo).getDescription()));
        PermissaoGrupo pg = new PermissaoDao().pesquisaPermissaoGrupo(l_grupo.getId(), lpc.getPermissao().getId());

        dao.begin();
        if (lpc.getCheck()) {
            if (pg != null) {
                dao.rollback();
                MensagemFlash.fatal("Atenção", "PERMISSÃO JÁ INSERIDA, TENTE NOVAMENTE!");
                return;
            }

            pg = new PermissaoGrupo(-1, lpc.getPermissao(), l_grupo);

            if (!dao.save(pg)) {
                dao.rollback();
                MensagemFlash.fatal("Atenção", "NÃO FOI POSSÍVEL ATUALIZAR PERMISSÃO DO GRUPO, TENTE NOVAMENTE!");
                return;
            }
        } else {
            if (pg == null) {
                dao.rollback();
                MensagemFlash.fatal("Atenção", "NÃO FOI POSSÍVEL ENCONTRAR PERMISSÃO, TENTE NOVAMENTE!");
                return;
            }

            if (!dao.remove(dao.find(pg))) {
                dao.rollback();
                MensagemFlash.fatal("Atenção", "NÃO FOI POSSÍVEL ATUALIZAR PERMISSÃO DO GRUPO, TENTE NOVAMENTE!");
                return;
            }
        }

        dao.commit();
        MensagemFlash.info("Sucesso", "PERMISSÃO ALTERADA!");
    }

    public void cadastrarNovoGrupo() {
        novoGrupo = new Grupo();
    }

    public final void loadListaGrupo() {
        listaGrupo.clear();
        indexListaGrupo = 0;

        List<Grupo> result = new PermissaoDao().listaGrupo();

        for (int i = 0; i < result.size(); i++) {
            listaGrupo.add(new SelectItem(
                    i,
                    result.get(i).getNome(),
                    Integer.toString(result.get(i).getId())
            ));
        }

        loadListaPagina();
    }

    public Boolean temPermissao(String descricao_permissao) {
        if (!Sessao.exist("sessao_usuario")) {
            return false;
        }

        Usuario u = (Usuario) Sessao.get("sessao_usuario");
        if (u.getAdministrador() == true) {
            return true;
        }
        // SE LISTA DE PERMISSAO FOR VAZIA ENTÃO NÃO TEM PERMISSÃO
        // SE VIER RESULTADO NA LISTA DE PERMISSÃO ENTÃO TEM PERMISSÃO

        return !new PermissaoDao().listaPermissaoUsuario(u.getGrupo().getId(), u.getId(), descricao_permissao).isEmpty();
    }

    public List<SelectItem> getListaGrupo() {
        return listaGrupo;
    }

    public void setListaGrupo(List<SelectItem> listaGrupo) {
        this.listaGrupo = listaGrupo;
    }

    public Integer getIndexListaGrupo() {
        return indexListaGrupo;
    }

    public void setIndexListaGrupo(Integer indexListaGrupo) {
        this.indexListaGrupo = indexListaGrupo;
    }

    public PesquisaPermissao getPesquisaPermissao() {
        return pesquisaPermissao;
    }

    public void setPesquisaPermissao(PesquisaPermissao pesquisaPermissao) {
        this.pesquisaPermissao = pesquisaPermissao;
    }

    public List<ListaPagina> getListaPagina() {
        return listaPagina;
    }

    public void setListaPagina(List<ListaPagina> listaPagina) {
        this.listaPagina = listaPagina;
    }

    public Grupo getNovoGrupo() {
        return novoGrupo;
    }

    public void setNovoGrupo(Grupo novoGrupo) {
        this.novoGrupo = novoGrupo;
    }

    public class PesquisaPermissao {

        private String descricao;

        public PesquisaPermissao() {
            this.descricao = "";
        }

        public PesquisaPermissao(String descricao) {
            this.descricao = descricao;
        }

        public String getDescricao() {
            return descricao;
        }

        public void setDescricao(String descricao) {
            this.descricao = descricao;
        }
    }

    public class ListaPagina {

        private Pagina pagina;
        private List<ListaPermissaoCheck> listaPermissaoCheck;

        public ListaPagina() {
            this.pagina = new Pagina();
            this.listaPermissaoCheck = new ArrayList();
        }

        public ListaPagina(Pagina pagina, List<ListaPermissaoCheck> listaPermissaoCheck) {
            this.pagina = pagina;
            this.listaPermissaoCheck = listaPermissaoCheck;
        }

        public Pagina getPagina() {
            return pagina;
        }

        public void setPagina(Pagina pagina) {
            this.pagina = pagina;
        }

        public List<ListaPermissaoCheck> getListaPermissaoCheck() {
            return listaPermissaoCheck;
        }

        public void setListaPermissaoCheck(List<ListaPermissaoCheck> listaPermissaoCheck) {
            this.listaPermissaoCheck = listaPermissaoCheck;
        }
    }

    public class ListaPermissaoCheck {

        private Boolean check;
        private Permissao permissao;

        public ListaPermissaoCheck() {
            this.check = false;
            this.permissao = new Permissao();
        }

        public ListaPermissaoCheck(Boolean check, Permissao permissao) {
            this.check = check;
            this.permissao = permissao;
        }

        public Boolean getCheck() {
            return check;
        }

        public void setCheck(Boolean check) {
            this.check = check;
        }

        public Permissao getPermissao() {
            return permissao;
        }

        public void setPermissao(Permissao permissao) {
            this.permissao = permissao;
        }
    }

}
