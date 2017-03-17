/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.factoring.controller;

import br.com.factoring.connections.Dao;
import br.com.factoring.dao.EnderecoDao;
import br.com.factoring.dao.LancamentoDao;
import br.com.factoring.dao.PessoaDao;
import br.com.factoring.model.Banco;
import br.com.factoring.model.Cidade;
import br.com.factoring.model.Emitente;
import br.com.factoring.model.FTipoDocumento;
import br.com.factoring.model.Movimento;
import br.com.factoring.model.Pessoa;
import br.com.factoring.model.TipoDocumento;
import br.com.factoring.utils.AnaliseString;
import br.com.factoring.utils.MensagemFlash;
import br.com.factoring.utils.Moeda;
import br.com.factoring.utils.Redirectx;
import br.com.factoring.utils.Sessao;
import br.com.factoring.utils.ValidaDocumento;
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
public class LancamentoController implements Serializable {

    private PesquisaLancamento pesquisaLancamento = new PesquisaLancamento();

    private Integer indexListaTipoDocumento = 0;
    private List<SelectItem> listaTipoDocumento = new ArrayList();

    private Integer indexListaBanco = 0;
    private List<SelectItem> listaBanco = new ArrayList();

    private Integer indexListaUF = 0;
    private List<SelectItem> listaUF = new ArrayList();

    private Integer indexListaCidade = 0;
    private List<SelectItem> listaCidade = new ArrayList();

    private Movimento movimento = new Movimento();

    private Integer indexListaTipoDocumentoEmitente = 0;
    private List<SelectItem> listaTipoDocumentoEmitente = new ArrayList();

    private List<DetalheMovimentosEmitente> listaDetalheMovimentosEmitente = new ArrayList();

    public LancamentoController() {
        loadListaTipoDocumento();
        loadListaTipoDocumentoEmitente();
        loadListaBanco();
        loadListaUF();
    }

    public void loadPagina() {
        if (!FacesContext.getCurrentInstance().isPostback()) {

            if (!new PermissaoController().temPermissao("cadastro_lancamento")) {
                Redirectx.go("dashboard");
            }
        }
    }

    public String paginaLancamento() {
        Sessao.put("lancamentoController", new LancamentoController());
        return "lancamento";
    }

    public void loadListaMovimentoEmitente() {
        listaDetalheMovimentosEmitente.clear();
        if (movimento.getEmitente() != null && movimento.getEmitente().getId() != -1) {
            List<Object[]> list_result = new LancamentoDao().listaMovimentosEmitente(movimento.getEmitente().getId());

            for (int i = 0; i < list_result.size(); i++) {
                listaDetalheMovimentosEmitente.add(
                        new DetalheMovimentosEmitente(
                                ((Long) list_result.get(i)[0]).intValue(),
                                (String) list_result.get(i)[1],
                                Moeda.converteUS$(Moeda.converteR$Double((Double) list_result.get(i)[2]))
                        )
                );
            }
        }
    }

    public void pesquisaEmitente() {
        LancamentoDao dao = new LancamentoDao();
        Emitente result = dao.pesquisaEmitente(movimento.getEmitente().getDocumento());

        if (result.getId() != -1) {
            movimento.setEmitente(result);
            loadListaMovimentoEmitente();
        }
    }

    public String getMaskDocumentoEmitente() {
        if (Integer.valueOf(listaTipoDocumentoEmitente.get(indexListaTipoDocumentoEmitente).getDescription()) == 1) {
            // CPF
            return "999.999.999-99";
        } else if (Integer.valueOf(listaTipoDocumentoEmitente.get(indexListaTipoDocumentoEmitente).getDescription()) == 2) {
            // CNPJ
            return "99.999.999.9999/99";
        }
        return "";
    }

    public void salvar() {
        LancamentoDao ldao = new LancamentoDao();
        Dao dao = new Dao();

        if (movimento.getDocumento().isEmpty()) {
            MensagemFlash.warn("Atenção", "DIGITE UM NÚMERO DE DOCUMENTO!");
            return;
        }

        if (movimento.getVencimentoString().isEmpty()) {
            MensagemFlash.warn("Atenção", "DIGITE UM VENCIMENTO!");
            return;
        }

        if (movimento.getVencimentoOriginalString().isEmpty()) {
            MensagemFlash.warn("Atenção", "DIGITE UM VENCIMENTO ORIGINAL!");
            return;
        }

        if (movimento.getValor() < 0) {
            MensagemFlash.warn("Atenção", "DIGITE UM VALOR!");
            return;
        }

        FTipoDocumento ftipo = (FTipoDocumento) dao.find(new FTipoDocumento(), Integer.valueOf(listaTipoDocumento.get(indexListaTipoDocumento).getDescription()));
        movimento.setTipoDocumento(ftipo);

        if (Integer.valueOf(listaTipoDocumento.get(indexListaTipoDocumento).getDescription()) != 1) {
            movimento.setBanco(null);
            movimento.setAgencia("");
            movimento.setConta("");
            movimento.setPraca(null);
            movimento.setEmitente(null);
        } else {
            Banco banco = (Banco) dao.find(new Banco(), Integer.valueOf(listaBanco.get(indexListaBanco).getDescription()));
            movimento.setBanco(banco);

            if (movimento.getAgencia().isEmpty()) {
                MensagemFlash.warn("Atenção", "DIGITE UMA AGÊNCIA!");
                return;
            }

            if (movimento.getConta().isEmpty()) {
                MensagemFlash.warn("Atenção", "DIGITE UMA CONTA!");
                return;
            }

            if (movimento.getEmitente().getDocumento().isEmpty()) {
                MensagemFlash.warn("Atenção", "DIGITE UM DOCUMENTO PARA O EMITENTE!");
                return;
            }
            
            Integer id_tipo_documento = Integer.valueOf(listaTipoDocumentoEmitente.get(indexListaTipoDocumentoEmitente).getDescription());
            if (id_tipo_documento == 1) {
                if (!ValidaDocumento.isValidoCPF(movimento.getEmitente().getDocumento())) {
                    MensagemFlash.warn("Atenção", "DIGITE UM CPF VÁLIDO PARA O EMITENTE!");
                    return;
                }
            } else if (!ValidaDocumento.isValidoCNPJ(movimento.getEmitente().getDocumento())) {
                MensagemFlash.warn("Atenção", "DIGITE UM CNPJ VÁLIDO PARA O EMITENTE!");
                return;
            }

            if (movimento.getEmitente().getNome().isEmpty()) {
                MensagemFlash.warn("Atenção", "DIGITE O NOME DO EMITENTE!");
                return;
            }

            dao.begin();
            movimento.getEmitente().setTipoDocumento((TipoDocumento) dao.find(new TipoDocumento(), id_tipo_documento));
            if (movimento.getEmitente().getId() == -1) {
                if (!dao.save(movimento.getEmitente())) {
                    dao.rollback();
                    MensagemFlash.error("Erro", "NÃO FOI POSSÍVEL SALVAR EMITENTE!");
                    return;
                }
            } else if (!dao.update(movimento.getEmitente())) {
                dao.rollback();
                MensagemFlash.error("Erro", "NÃO FOI POSSÍVEL ATUALIZAR EMITENTE!");
                return;
            }
            dao.commit();
            if (movimento.getId() == -1) {
                if (ldao.pesquisaChequeEmitido(movimento.getPessoa().getId(), banco.getId(), movimento.getAgencia(), movimento.getConta(), movimento.getDocumento()) != null) {
                    MensagemFlash.fatal("Atenção", "ESTE CHEQUE JÁ EXISTE!");
                    return;
                }
            }

            Cidade praca = (Cidade) dao.find(new Cidade(), Integer.valueOf(listaCidade.get(indexListaCidade).getDescription()));
            movimento.setPraca(praca);
        }

        dao.begin();

        if (movimento.getId() == -1) {
            if (!dao.save(movimento)) {
                dao.rollback();
                MensagemFlash.error("Erro", "NÃO FOI POSSÍVEL SALVAR LANÇAMENTO!");
                return;
            }
        } else if (!dao.update(movimento)) {
            dao.rollback();
            MensagemFlash.error("Erro", "NÃO FOI POSSÍVEL ATUALIZAR LANÇAMENTO!");
            return;
        }

        dao.commit();

        pesquisaLancamento.loadListaMovimento();
        loadListaMovimentoEmitente();
        MensagemFlash.info("Sucesso", "LANÇAMENTO SALVO!");

    }

    public void editar(Movimento m) {
        movimento = m;

        for (int i = 0; i < listaTipoDocumento.size(); i++) {
            if (Integer.valueOf(listaTipoDocumento.get(i).getDescription()) == movimento.getTipoDocumento().getId()) {
                indexListaTipoDocumento = i;
            }
        }

        if (movimento.getBanco() != null) {
            for (int i = 0; i < listaBanco.size(); i++) {
                if (Integer.valueOf(listaBanco.get(i).getDescription()) == movimento.getBanco().getId()) {
                    indexListaBanco = i;
                }
            }

            for (int i = 0; i < listaUF.size(); i++) {
                if (listaUF.get(i).getDescription().equals(movimento.getPraca().getUf())) {
                    indexListaUF = i;
                }
            }

            loadListaCidade();
            for (int i = 0; i < listaCidade.size(); i++) {
                if (Integer.valueOf(listaCidade.get(i).getDescription()) == movimento.getPraca().getId()) {
                    indexListaCidade = i;
                }
            }

            for (int i = 0; i < listaTipoDocumentoEmitente.size(); i++) {
                if (Integer.valueOf(listaTipoDocumentoEmitente.get(i).getDescription()) == movimento.getEmitente().getTipoDocumento().getId()) {
                    indexListaTipoDocumentoEmitente = i;
                }
            }

            loadListaMovimentoEmitente();
        }
    }

    public void excluir() {
        Dao dao = new Dao();

        dao.begin();

        if (!dao.remove(movimento)) {
            dao.rollback();
            MensagemFlash.error("Erro", "NÃO FOI POSSÍVEL EXCLUIR ESTE LANÇAMENTO!");
            return;
        }

        dao.commit();
        MensagemFlash.info("Sucesso", "LANÇAMENTO SALVO!");

        pesquisaLancamento.loadListaMovimento();
        novoLancamento();
    }

    public void novoLancamento() {
        movimento = new Movimento();
        movimento.setPessoa(pesquisaLancamento.pessoa);

        indexListaTipoDocumento = 0;
        indexListaBanco = 0;

        loadListaUF();
        loadListaMovimentoEmitente();
    }

    public void atualizaVencimentoOriginal() {
        if (movimento.getId() == -1) {
            movimento.setVencimentoOriginal(movimento.getVencimento());
        }
    }

    public final void loadListaTipoDocumento() {
        indexListaTipoDocumento = 0;
        listaTipoDocumento.clear();

        List<FTipoDocumento> result = new LancamentoDao().listaTipoDocumento();

        for (int i = 0; i < result.size(); i++) {
            listaTipoDocumento.add(new SelectItem(
                    i,
                    result.get(i).getTipoDocumento(),
                    Integer.toString(result.get(i).getId()))
            );

        }
    }

    public final void loadListaTipoDocumentoEmitente() {
        indexListaTipoDocumentoEmitente = 0;
        listaTipoDocumentoEmitente.clear();

        List<TipoDocumento> result = new PessoaDao().listaTipoDocumento();

        for (int i = 0; i < result.size(); i++) {
            listaTipoDocumentoEmitente.add(new SelectItem(
                    i,
                    result.get(i).getDescricao(),
                    Integer.toString(result.get(i).getId()))
            );

        }
    }

    public final void loadListaBanco() {
        indexListaBanco = 0;
        listaBanco.clear();

        List<Banco> result = new LancamentoDao().listaBanco();

        for (int i = 0; i < result.size(); i++) {
            listaBanco.add(new SelectItem(
                    i,
                    result.get(i).getNumero() + " - " + result.get(i).getBanco(),
                    Integer.toString(result.get(i).getId()))
            );

        }
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

    public PesquisaLancamento getPesquisaLancamento() {
        if (Sessao.exist("pessoaToReturn")) {
            pesquisaLancamento.pessoa = (Pessoa) Sessao.get("pessoaToReturn", true);
            pesquisaLancamento.loadListaMovimento();
        }
        return pesquisaLancamento;
    }

    public void setPesquisaLancamento(PesquisaLancamento pesquisaLancamento) {
        this.pesquisaLancamento = pesquisaLancamento;
    }

    public List<SelectItem> getListaTipoDocumento() {
        return listaTipoDocumento;
    }

    public void setListaTipoDocumento(List<SelectItem> listaTipoDocumento) {
        this.listaTipoDocumento = listaTipoDocumento;
    }

    public Integer getIndexListaTipoDocumento() {
        return indexListaTipoDocumento;
    }

    public void setIndexListaTipoDocumento(Integer indexListaTipoDocumento) {
        this.indexListaTipoDocumento = indexListaTipoDocumento;
    }

    public Movimento getMovimento() {
        return movimento;
    }

    public void setMovimento(Movimento movimento) {
        this.movimento = movimento;
    }

    public Integer getIndexListaBanco() {
        return indexListaBanco;
    }

    public void setIndexListaBanco(Integer indexListaBanco) {
        this.indexListaBanco = indexListaBanco;
    }

    public List<SelectItem> getListaBanco() {
        return listaBanco;
    }

    public void setListaBanco(List<SelectItem> listaBanco) {
        this.listaBanco = listaBanco;
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

    public Integer getIndexListaTipoDocumentoEmitente() {
        return indexListaTipoDocumentoEmitente;
    }

    public void setIndexListaTipoDocumentoEmitente(Integer indexListaTipoDocumentoEmitente) {
        this.indexListaTipoDocumentoEmitente = indexListaTipoDocumentoEmitente;
    }

    public List<SelectItem> getListaTipoDocumentoEmitente() {
        return listaTipoDocumentoEmitente;
    }

    public void setListaTipoDocumentoEmitente(List<SelectItem> listaTipoDocumentoEmitente) {
        this.listaTipoDocumentoEmitente = listaTipoDocumentoEmitente;
    }

    public List<DetalheMovimentosEmitente> getListaDetalheMovimentosEmitente() {
        return listaDetalheMovimentosEmitente;
    }

    public void setListaDetalheMovimentosEmitente(List<DetalheMovimentosEmitente> listaDetalheMovimentosEmitente) {
        this.listaDetalheMovimentosEmitente = listaDetalheMovimentosEmitente;
    }

    public class PesquisaLancamento {

        private Pessoa pessoa;
        private String tipoVencimento;
        private List<Movimento> listaMovimento;
        private Float somaValores;
        private String ordem;
        private Integer indexListaTipoDocumento;
        private List<SelectItem> listaTipoDocumento;

        public PesquisaLancamento() {
            this.pessoa = new Pessoa();
            this.tipoVencimento = "avencer";
            this.listaMovimento = new ArrayList();
            this.somaValores = new Float(0);
            this.ordem = "lancamento";
            this.indexListaTipoDocumento = 0;
            this.listaTipoDocumento = new ArrayList();
            this.loadListaTipoDocumento();
        }

        public PesquisaLancamento(Pessoa pessoa, String tipoVencimento, List<Movimento> listaMovimento, Float somaValores, String ordem, Integer indexListaTipoDocumento, List<SelectItem> listaTipoDocumento) {
            this.pessoa = pessoa;
            this.tipoVencimento = tipoVencimento;
            this.listaMovimento = listaMovimento;
            this.somaValores = somaValores;
            this.ordem = ordem;
            this.indexListaTipoDocumento = indexListaTipoDocumento;
            this.listaTipoDocumento = listaTipoDocumento;
        }

        public final void loadListaTipoDocumento() {
            this.indexListaTipoDocumento = 0;
            this.listaTipoDocumento.clear();

            List<FTipoDocumento> result = new LancamentoDao().listaTipoDocumento();
            this.listaTipoDocumento.add(new SelectItem(0, "TODOS", "0"));

            for (int i = 0; i < result.size(); i++) {
                this.listaTipoDocumento.add(new SelectItem(
                        i + 1,
                        result.get(i).getTipoDocumento(),
                        Integer.toString(result.get(i).getId()))
                );

            }
        }

        public void loadListaMovimento() {
            this.listaMovimento.clear();
            this.somaValores = new Float(0);

            this.listaMovimento = new LancamentoDao().listaMovimento(PesquisaLancamento.this);

            for (Movimento m : this.listaMovimento) {
                somaValores = Moeda.somaValores(somaValores, m.getValor());
            }

        }

        public void novo() {
            pesquisaLancamento = new PesquisaLancamento();
        }

        public Pessoa getPessoa() {
            return pessoa;
        }

        public void setPessoa(Pessoa pessoa) {
            this.pessoa = pessoa;
        }

        public String getTipoVencimento() {
            return tipoVencimento;
        }

        public void setTipoVencimento(String tipoVencimento) {
            this.tipoVencimento = tipoVencimento;
        }

        public List<Movimento> getListaMovimento() {
            return listaMovimento;
        }

        public void setListaMovimento(List<Movimento> listaMovimento) {
            this.listaMovimento = listaMovimento;
        }

        public Float getSomaValores() {
            return somaValores;
        }

        public void setSomaValores(Float somaValores) {
            this.somaValores = somaValores;
        }

        public String getSomaValoresString() {
            return Moeda.converteR$Float(somaValores);
        }

        public void setSomaValoresString(String somaValoresString) {
            this.somaValores = Moeda.converteUS$(somaValoresString);
        }

        public String getOrdem() {
            return ordem;
        }

        public void setOrdem(String ordem) {
            this.ordem = ordem;
        }

        public Integer getIndexListaTipoDocumento() {
            return indexListaTipoDocumento;
        }

        public void setIndexListaTipoDocumento(Integer indexListaTipoDocumento) {
            this.indexListaTipoDocumento = indexListaTipoDocumento;
        }

        public List<SelectItem> getListaTipoDocumento() {
            return listaTipoDocumento;
        }

        public void setListaTipoDocumento(List<SelectItem> listaTipoDocumento) {
            this.listaTipoDocumento = listaTipoDocumento;
        }

    }

    public class DetalheMovimentosEmitente {

        private Integer quantidade;
        private String status;
        private Float valor_total;

        public DetalheMovimentosEmitente() {
            this.quantidade = 0;
            this.status = "";
            this.valor_total = new Float(0);
        }

        public DetalheMovimentosEmitente(Integer quantidade, String status, Float valor_total) {
            this.quantidade = quantidade;
            this.status = status;
            this.valor_total = valor_total;
        }

        public Integer getQuantidade() {
            return quantidade;
        }

        public void setQuantidade(Integer quantidade) {
            this.quantidade = quantidade;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public Float getValor_total() {
            return valor_total;
        }

        public void setValor_total(Float valor_total) {
            this.valor_total = valor_total;
        }

        public String getValor_totalString() {
            return Moeda.converteR$Float(valor_total);
        }

        public void setValor_totalString(String valor_totalString) {
            this.valor_total = Moeda.converteUS$(valor_totalString);
        }
    }
}
