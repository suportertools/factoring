/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.factoring.find;

import br.com.factoring.connections.Dao;
import br.com.factoring.dao.EnderecoDao;
import br.com.factoring.model.Cidade;
import br.com.factoring.model.Endereco;
import br.com.factoring.model.Pessoa;
import br.com.factoring.model.TipoEndereco;
import br.com.factoring.utils.CEPService;
import br.com.factoring.utils.MensagemFlash;
import br.com.factoring.utils.PF;
import br.com.factoring.utils.Sessao;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.model.SelectItem;

/**
 *
 * @author Claudemir Rtools
 */
@ManagedBean
@SessionScoped
public class PesquisaEnderecoFind implements Serializable {

    private Endereco endereco;
    private List<Endereco> listaEndereco;

    private Integer indexTipoEndereco;
    private List<SelectItem> listaTipoEndereco;

    private Boolean enderecoNaoEncontrado;
    private Boolean cadastrarEndereco;

    private Integer indexLogradouro;
    private List<SelectItem> listaLogradouro;

    private Integer indexListaUF;
    private List<SelectItem> listaUF;

    private Integer indexListaCidade;
    private List<SelectItem> listaCidade;


    /* 
    NOVA ESTRUTURA
     */
    private Pessoa pessoa = new Pessoa();
    private Step step = new Step();
    private String formToUpdate = "";

    /*
    FIM NOVA ESTRUTURA
     */
    public PesquisaEnderecoFind() {
        this.endereco = new Endereco();
        this.listaEndereco = new ArrayList();
        this.indexTipoEndereco = 0;
        this.listaTipoEndereco = new ArrayList();

        loadListaTipoEndereco();
        //loadListaEndereco();

        this.enderecoNaoEncontrado = false;
        this.cadastrarEndereco = false;
        this.indexLogradouro = 0;
        this.listaLogradouro = new ArrayList();
        loadListaLogradouro();

        this.indexListaUF = 0;
        this.listaUF = new ArrayList();
        this.indexListaCidade = 0;
        this.listaCidade = new ArrayList();
        this.formToUpdate = "";
    }

    public void avancar() {
        switch (step.ATUAL) {
            case Step.PESQUISAR_CEP:
                loadListaEndereco();
                break;

            case Step.SELECIONAR_CEP:
                step.ATUAL = Step.SALVAR_CEP;
                break;

            case Step.CADASTRAR_CEP:
                break;

            case Step.SALVAR_CEP:
                salvarEndereco();
                break;

            default:
                break;
        }
    }

    public void adicionarEndereco(Pessoa p, String form_update) {
        Sessao.put("pesquisaEnderecoFind", new PesquisaEnderecoFind());
        
        //endereco = new Endereco();
        
        pessoa = p;
        formToUpdate = form_update;

        step = new Step();

        PF.openDialog("dlg_pesquisa_endereco");
        PF.update("form_pesquisa_endereco:panel_pesquisar_endereco");
    }

    
    public void actCadastrarEndereco() {
        if (endereco.getCep().isEmpty()){
            MensagemFlash.warn("Atenção", "DIGITE UM CEP PARA CADASTRO!");
            return;
        }
        step.ATUAL = Step.CADASTRAR_CEP;
        loadListaUF();
    }

    
    public void selecionarCEP(Endereco e) {
        endereco = e;
        step.ATUAL = Step.SALVAR_CEP;
    }

    public void novo() {
        Pessoa p = endereco.getPessoa();
        endereco = new Endereco();
        endereco.setPessoa(p);

        enderecoNaoEncontrado = false;
        cadastrarEndereco = false;
        loadListaTipoEndereco();
        loadListaEndereco();
        loadListaLogradouro();
    }

    public void selecionar(Endereco e, String up_form) {
        endereco = e;
        for (int i = 0; i < listaTipoEndereco.size(); i++) {
            if (endereco.getTipoEndereco().getId() == Integer.valueOf(listaTipoEndereco.get(i).getDescription())) {
                indexTipoEndereco = i;
                break;
            }
        }
        
        formToUpdate = up_form;

        PF.openDialog("dlg_pesquisa_endereco");
        PF.update("form_pesquisa_endereco");
        //fisica.setJuridica(j);
    }

    public void salvarEndereco() {
        if (endereco.getEndereco().isEmpty()) {
            MensagemFlash.fatal("Atenção", "DIGITE UM ENDEREÇO VÁLIDO!");
            return;
        }

        if (endereco.getNumero().isEmpty()) {
            MensagemFlash.fatal("Atenção", "DIGITE UM NÚMERO PARA O ENDEREÇO!");
            return;
        }

        if (endereco.getBairro().isEmpty()) {
            MensagemFlash.fatal("Atenção", "DIGITE UM BAIRRO VÁLIDO!");
            return;
        }

        Dao dao = new Dao();

        if (cadastrarEndereco) {
            endereco.setLogradouro(listaLogradouro.get(indexLogradouro).getLabel());

            endereco.setCidade((Cidade) dao.find(new Cidade(), Integer.valueOf(listaCidade.get(indexListaCidade).getDescription())));

            endereco.setTipoEndereco((TipoEndereco) new Dao().find(new TipoEndereco(), Integer.valueOf(listaTipoEndereco.get(indexTipoEndereco).getDescription())));
        }

        dao.begin();
        if (endereco.getId() == -1) {
            if (!dao.save(endereco)) {
                dao.rollback();
                MensagemFlash.fatal("Erro", "NÃO FOI POSSÍVEL SALVAR O ENDEREÇO!");
                return;
            }
        } else if (!dao.update(endereco)) {
            dao.rollback();
            MensagemFlash.fatal("Erro", "NÃO FOI POSSÍVEL ALTERAR O ENDEREÇO!");
            return;
        }

        dao.commit();

        Sessao.put("enderecoToReturn", endereco);
        MensagemFlash.info("Sucesso", "ENDEREÇO SALVO!");

        novo();

        PF.closeDialog("dlg_pesquisa_endereco");
        PF.update(formToUpdate);
    }

    public void excluirEndereco(Endereco e) {
        if (e != null) {
            endereco = e;
        }

        Dao dao = new Dao();

        dao.begin();

        if (!dao.remove(endereco)) {
            dao.rollback();
            MensagemFlash.error("Erro", "NÃO FOI POSSÍVEL EXCLUIR ENDEREÇO!");
            return;
        }

        dao.commit();

        loadListaEndereco();
        endereco = new Endereco();
        Sessao.put("enderecoToReturn", endereco);

        PF.update(formToUpdate);

        if (e == null) {
            PF.closeDialog("dlg_pesquisa_endereco");
        }
        MensagemFlash.info("Sucesso", "ENDEREÇO DELETADO!");
    }

    public final void loadListaUF() {
        indexListaUF = 0;
        listaUF.clear();

        List<String> result = new EnderecoDao().listaUF();

        for (int i = 0; i < result.size(); i++) {
            listaUF.add(new SelectItem(
                    i,
                    result.get(i),
                    result.get(i))
            );
        }

        loadListaCidade();
    }

    public final void loadListaCidade() {
        indexListaCidade = 0;
        listaCidade.clear();

        List<Cidade> result = new EnderecoDao().listaCidade(listaUF.get(indexListaUF).getDescription());

        for (int i = 0; i < result.size(); i++) {
            listaCidade.add(new SelectItem(
                    i,
                    result.get(i).getCidade(),
                    Integer.toString(result.get(i).getId()))
            );

        }
    }

    public final void loadListaLogradouro() {
        indexLogradouro = 0;
        listaLogradouro.clear();

        List<Object> result = new EnderecoDao().listaLogradouro();

        for (int i = 0; i < result.size(); i++) {
            String linha = (String) result.get(i);
            listaLogradouro.add(new SelectItem(i, linha, linha));
        }
    }

    public void salvarCadastrarEndereco() {
        novo();
    }

    public final void loadListaEndereco() {
        listaEndereco.clear();
        if (!endereco.getCep().isEmpty()) {

            Endereco enderecoReturn = CEPService.procurar(endereco.getCep());

            if (enderecoReturn == null) {
                enderecoReturn = new EnderecoDao().pesquisaEndereco(endereco.getCep());

                if (enderecoReturn == null) {
                    MensagemFlash.warn("Atenção", "CEP não encontrado!");
                    enderecoNaoEncontrado = true;
                    step.ATUAL = Step.CADASTRAR_CEP;
                    return;
                }
                enderecoReturn.setComplemento("");
                enderecoReturn.setNumero("");
            }

            endereco = new Endereco(
                    -1,
                    pessoa,
                    enderecoReturn.getTipoEndereco(),
                    enderecoReturn.getLogradouro(),
                    enderecoReturn.getEndereco(),
                    enderecoReturn.getNumero(),
                    enderecoReturn.getComplemento(),
                    enderecoReturn.getBairro(),
                    enderecoReturn.getCep(),
                    enderecoReturn.getCidade()
            );

            selecionaTipoEndereco();
            listaEndereco.add(endereco);
            
            step.ATUAL = Step.SELECIONAR_CEP;
            
            return;
        }

        MensagemFlash.warn("Atenção", "Digite um CEP para pesquisar!");
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

    public Boolean getEnderecoNaoEncontrado() {
        return enderecoNaoEncontrado;
    }

    public void setEnderecoNaoEncontrado(Boolean enderecoNaoEncontrado) {
        this.enderecoNaoEncontrado = enderecoNaoEncontrado;
    }

    public Boolean getCadastrarEndereco() {
        return cadastrarEndereco;
    }

    public void setCadastrarEndereco(Boolean cadastrarEndereco) {
        this.cadastrarEndereco = cadastrarEndereco;
    }

    public Integer getIndexLogradouro() {
        return indexLogradouro;
    }

    public void setIndexLogradouro(Integer indexLogradouro) {
        this.indexLogradouro = indexLogradouro;
    }

    public List<SelectItem> getListaLogradouro() {
        return listaLogradouro;
    }

    public void setListaLogradouro(List<SelectItem> listaLogradouro) {
        this.listaLogradouro = listaLogradouro;
    }

    public Integer getIndexListaUF() {
        return indexListaUF;
    }

    public void setIndexListaUF(Integer indexListaUF) {
        this.indexListaUF = indexListaUF;
    }

    public List<SelectItem> getListaUF() {
        return listaUF;
    }

    public void setListaUF(List<SelectItem> listaUF) {
        this.listaUF = listaUF;
    }

    public Integer getIndexListaCidade() {
        return indexListaCidade;
    }

    public void setIndexListaCidade(Integer indexListaCidade) {
        this.indexListaCidade = indexListaCidade;
    }

    public List<SelectItem> getListaCidade() {
        return listaCidade;
    }

    public void setListaCidade(List<SelectItem> listaCidade) {
        this.listaCidade = listaCidade;
    }

    public Step getStep() {
        return step;
    }

    public void setStep(Step step) {
        this.step = step;
    }

    public String getFormToUpdate() {
        return formToUpdate;
    }

    public void setFormToUpdate(String formToUpdate) {
        this.formToUpdate = formToUpdate;
    }

    public class Step {



        public int ATUAL = 0;
        public static final int PESQUISAR_CEP = 0;
        private static final int SELECIONAR_CEP = 1;
        private static final int CADASTRAR_CEP = 2;
        private static final int SALVAR_CEP = 3;

        public int getATUAL() {
            return ATUAL;
        }

        public void setATUAL(int ATUAL) {
            this.ATUAL = ATUAL;
        }

        public final int getPESQUISAR_CEP() {
            return PESQUISAR_CEP;
        }
        
        public final int getSELECIONAR_CEP() {
            return SELECIONAR_CEP;
        }
        
        public final int getCADASTRAR_CEP() {
            return CADASTRAR_CEP;
        }

        public final int getSALVAR_CEP() {
            return SALVAR_CEP;
        }

    }
}
