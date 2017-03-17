/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.factoring.controller;

import br.com.factoring.connections.Dao;
import br.com.factoring.dao.PermissaoDao;
import br.com.factoring.dao.UsuarioDao;
import br.com.factoring.model.Grupo;
import br.com.factoring.model.Usuario;
import br.com.factoring.utils.MensagemFlash;
import br.com.factoring.utils.Redirectx;
import br.com.factoring.utils.Sessao;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 *
 * @author Claudemir Rtools
 */
@ManagedBean
@SessionScoped
public class UsuarioController implements Serializable {

    private Usuario usuario = new Usuario();
    private Usuario usuarioSessao = new Usuario();
    private String sessaoMessageLogin = "";
    private List<Usuario> listaUsuario = new ArrayList();
    private String confirmarSenha = "";

    private String novaSenha = "";
    private String confirmaNovaSenha = "";

    private List<SelectItem> listaGrupo = new ArrayList();
    private Integer indexListaGrupo = 0;

    public UsuarioController() {
        loadListaGrupo();
    }

    public void loadUsuario() {
        if (!FacesContext.getCurrentInstance().isPostback()) {
            if (!new PermissaoController().temPermissao("cadastro_usuario")) {
                Redirectx.go("dashboard");
            }
        }
    }

    public void loadPagina() {
        if (!FacesContext.getCurrentInstance().isPostback()) {
            loadListaUsuario();
            
            if (!new PermissaoController().temPermissao("lista_usuario")) {
                Redirectx.go("dashboard");
            }
        }
    }

    public final void loadListaGrupo() {
        listaGrupo.clear();
        List<Grupo> result = new PermissaoDao().listaGrupo();

        for (int i = 0; i < result.size(); i++) {
            listaGrupo.add(new SelectItem(
                    i,
                    result.get(i).getNome(),
                    Integer.toString(result.get(i).getId())
            ));
        }
    }

    public void alterarSenha() {
        if (novaSenha.isEmpty() || confirmaNovaSenha.isEmpty()){
            MensagemFlash.fatal("Atenção", "DIGITE AS SENHAS VÁLIDAS!");
            return;
        }
        
        if (!novaSenha.equals(confirmaNovaSenha)) {
            MensagemFlash.fatal("Atenção", "SENHAS NÃO CORRESPONDEM!");
            return;
        }

        Dao dao = new Dao();

        dao.begin();
        usuario.setSenha(novaSenha);
        if (!dao.update(usuario)) {
            dao.rollback();
            MensagemFlash.fatal("Atenção", "ERRO AO ATUALIZAR USUÁRIO!");
            return;
        }
        dao.commit();

        MensagemFlash.info("Sucesso", "SENHA ALTERADA!");
    }

    public String paginaCadastrarUsuario() {
        usuario = new Usuario();
        return "usuario";
    }

    public String editar(Usuario u) {
        usuario = u;
        
        for (int i = 0; i < listaGrupo.size(); i++){
            if (usuario.getGrupo().getId() == Integer.valueOf(listaGrupo.get(i).getDescription())){
                indexListaGrupo = i;
            }
        }
        return "usuario";
    }

    public void loadListaUsuario() {
        listaUsuario = new UsuarioDao().listaUsuario();
    }

    public void loadPaginaCadastro() {
        if (!FacesContext.getCurrentInstance().isPostback()) {
            if ((Sessao.exist("sessao_usuario")) || usuarioSessao.getId() != -1) {
                entrar();
                try {
                    FacesContext.getCurrentInstance().getExternalContext().redirect("dashboard.xhtml");
                } catch (Exception e) {
                    e.getMessage();
                }
            } else {
                usuarioSessao = new Usuario();
                confirmarSenha = "";
            }
        }
    }

    public String salvar() {
        UsuarioDao udao = new UsuarioDao();

        if (usuario.getNome().isEmpty()) {
            MensagemFlash.fatal("Atenção", "DIGITE SEU NOME!");
            return null;
        }

        if (usuario.getLogin().isEmpty()) {
            MensagemFlash.fatal("Atenção", "DIGITE UM USUÁRIO PARA ESTA PESSOA!");
            return null;
        }

        if (usuario.getId() == -1) {
            Usuario u = udao.pesquisaUsuarioLoginExiste(null, usuario.getLogin());
            if (u != null) {
                MensagemFlash.fatal("Atenção", "USUÁRIO JÁ EXISTE, ESCOLHA OUTRO!");
                usuario.setLogin("");
                return null;
            }
            if (usuario.getSenha().isEmpty()) {
                MensagemFlash.fatal("Atenção", "DIGITE UMA SENHA DE USUÁRIO PARA ESTA PESSOA!");
                return null;
            }

            if (!usuario.getSenha().equals(confirmarSenha)) {
                MensagemFlash.fatal("Atenção", "SENHAS NÃO CORRESPONDEM!");
                return null;
            }
        } else {
            Usuario u = udao.pesquisaUsuarioLoginExiste(usuario.getId(), usuario.getLogin());
            if (u != null) {
                MensagemFlash.fatal("Atenção", "USUÁRIO JÁ EXISTE, ESCOLHA OUTRO!");
                usuario.setLogin("");
                return null;
            }
        }
        Dao dao = new Dao();

        usuario.setGrupo((Grupo) dao.find(new Grupo(), Integer.valueOf(listaGrupo.get(indexListaGrupo).getDescription())));

        dao.begin();

        if (usuario.getId() == -1) {
            if (!dao.save(usuario)) {
                dao.rollback();
                MensagemFlash.fatal("Atenção", "ERRO AO SALVAR USUÁRIO!");
                return null;
            }
            MensagemFlash.info("Sucesso", "NOVO USUÁRIO CADASTRADO!");
        } else {
            if (!dao.update(usuario)) {
                dao.rollback();
                MensagemFlash.fatal("Atenção", "ERRO AO SALVAR USUÁRIO!");
                return null;
            }
            MensagemFlash.info("Sucesso", "USUÁRIO ATUALIZADO!");
        }

        dao.commit();

        return null;
    }

    public void novo() {
        usuario = new Usuario();
    }

    public void excluir() {
        Dao dao = new Dao();

        dao.begin();

        if (!dao.remove(usuario)) {
            dao.rollback();
            MensagemFlash.fatal("Atenção", "ERRO AO EXCLUIR USUÁRIO!");
            return;
        }

        dao.commit();
        usuario = new Usuario();
        MensagemFlash.info("Sucesso", "USUÁRIO EXCLUÍDO!");
    }

    public String entrar() {
        limpar_sessao_antes_de_iniciar();

        UsuarioDao dao = new UsuarioDao();

        Usuario u = dao.pesquisaUsuario(usuario.getLogin(), usuario.getSenha());

        if (u != null) {
            Sessao.put("sessao_usuario", u);
            Object redirect_page = Sessao.get("sessao_redirect_page", true);

            sessaoMessageLogin = "";
            usuarioSessao = u;

            if (redirect_page != null) {
                return redirect_page.toString().replace(".xhtml", "");
            } else {
                return "dashboard";
            }
        } else {
            MensagemFlash.fatal("Atenção", "USUÁRIO E/OU SENHA INVÁLIDOS");
            return null;
        }
    }

    public void sair() {
        try {
            FacesContext conext = FacesContext.getCurrentInstance();

            HttpSession session = (HttpSession) conext.getExternalContext().getSession(false);

            session.invalidate();

            FacesContext.getCurrentInstance().getExternalContext().redirect("index.xhtml");
        } catch (Exception e) {
            e.getMessage();
        }
    }

    public void limpar_sessao_antes_de_iniciar() {

    }

    public void validacao() throws IOException {
        HttpServletRequest pagina_requerida = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();

        String uri_pagina = pagina_requerida.getRequestURI().replace("/factoring/", "");

        if (!Sessao.exist("sessao_usuario") && (!uri_pagina.contains("index") || uri_pagina.equals(""))) {
            Sessao.put("sessao_redirect_page", uri_pagina);
            //Sessao.put("sessao_message_login", "FAÇA O LOGIN PARA TER ACESSO A ESSA PÁGINA");
            MensagemFlash.fatal("Atenção", "FAÇA O LOGIN PARA TER ACESSO A ESSA PÁGINA");
            FacesContext.getCurrentInstance().getExternalContext().redirect("index.xhtml");
            return;
        }

        if (Sessao.exist("sessao_usuario") && (uri_pagina.contains("index") || uri_pagina.equals(""))) {
            FacesContext.getCurrentInstance().getExternalContext().redirect("dashboard.xhtml");
            return;
        }

    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public String getSessaoMessageLogin() {
        if (Sessao.exist("sessao_message_login")) {
            sessaoMessageLogin = (String) Sessao.get("sessao_message_login", true);
        }
        return sessaoMessageLogin;
    }

    public void setSessaoMessageLogin(String sessaoMessageLogin) {
        this.sessaoMessageLogin = sessaoMessageLogin;
    }

    public List<Usuario> getListaUsuario() {
        return listaUsuario;
    }

    public void setListaUsuario(List<Usuario> listaUsuario) {
        this.listaUsuario = listaUsuario;
    }

    public Usuario getUsuarioSessao() {
        return usuarioSessao;
    }

    public void setUsuarioSessao(Usuario usuarioSessao) {
        this.usuarioSessao = usuarioSessao;
    }

    public String getConfirmarSenha() {
        return confirmarSenha;
    }

    public void setConfirmarSenha(String confirmarSenha) {
        this.confirmarSenha = confirmarSenha;
    }

    public String getNovaSenha() {
        return novaSenha;
    }

    public void setNovaSenha(String novaSenha) {
        this.novaSenha = novaSenha;
    }

    public String getConfirmaNovaSenha() {
        return confirmaNovaSenha;
    }

    public void setConfirmaNovaSenha(String confirmaNovaSenha) {
        this.confirmaNovaSenha = confirmaNovaSenha;
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
}
