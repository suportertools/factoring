package br.com.factoring.model;

import br.com.factoring.dao.EnderecoDao;
import br.com.factoring.utils.Datas;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.persistence.*;

@Entity
@Table(name = "pes_pessoa")
public class Pessoa implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;
    @Column(name = "ds_nome", length = 200)
    private String nome;
    @Temporal(TemporalType.DATE)
    @Column(name = "dt_cadastro")
    private Date cadastro;
    @JoinColumn(name = "id_tipo_documento", referencedColumnName = "id", nullable = false)
    @ManyToOne
    private TipoDocumento tipoDocumento;
    @Column(name = "ds_documento", length = 25)
    private String documento;
    @Column(name = "ds_telefone1", length = 20)
    private String telefone1;
    @Column(name = "ds_telefone2", length = 20)
    private String telefone2;
    @Column(name = "ds_telefone3", length = 20)
    private String telefone3;
    @Column(name = "ds_observacao", length = 1000)
    private String observacao;

    public Pessoa() {
        this.id = -1;
        this.nome = "";
        this.cadastro = Datas.dataHoje();
        this.tipoDocumento = new TipoDocumento();
        this.documento = "";
        this.telefone1 = "";
        this.telefone2 = "";
        this.telefone3 = "";
        this.observacao = "";
    }

    public Pessoa(int id, String nome, Date cadastro, TipoDocumento tipoDocumento, String documento, String telefone1, String telefone2, String telefone3, String observacao) {
        this.id = id;
        this.nome = nome;
        this.cadastro = cadastro;
        this.tipoDocumento = tipoDocumento;
        this.documento = documento;
        this.telefone1 = telefone1;
        this.telefone2 = telefone2;
        this.telefone3 = telefone3;
        this.observacao = observacao;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
    
    
    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Date getCadastro() {
        return cadastro;
    }

    public void setCadastro(Date cadastro) {
        this.cadastro = cadastro;
    }

    public String getCadastroString() {
        return Datas.converteData(cadastro);
    }

    public void setCadastroString(String cadastroString) {
        this.cadastro = Datas.converte(cadastroString);
    }

    public TipoDocumento getTipoDocumento() {
        return tipoDocumento;
    }

    public void setTipoDocumento(TipoDocumento tipoDocumento) {
        this.tipoDocumento = tipoDocumento;
    }

    public String getDocumento() {
        return documento;
    }

    public void setDocumento(String documento) {
        this.documento = documento;
    }

    public String getTelefone1() {
        return telefone1;
    }

    public void setTelefone1(String telefone1) {
        this.telefone1 = telefone1;
    }

    public String getTelefone2() {
        return telefone2;
    }

    public void setTelefone2(String telefone2) {
        this.telefone2 = telefone2;
    }

    public String getTelefone3() {
        return telefone3;
    }

    public void setTelefone3(String telefone3) {
        this.telefone3 = telefone3;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    public List<Endereco> getListaEndereco(){
        return new EnderecoDao().listaEndereco(this.id);
    }
    

}
