package br.com.factoring.model;

import java.io.Serializable;
import javax.persistence.*;

@Entity
@Table(name = "pes_endereco")
public class Endereco implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;
    @JoinColumn(name = "id_pessoa", referencedColumnName = "id", nullable = false)
    @ManyToOne
    private Pessoa pessoa;
    @JoinColumn(name = "id_tipo_endereco", referencedColumnName = "id", nullable = false)
    @ManyToOne
    private TipoEndereco tipoEndereco;
    @Column(name = "ds_logradouro", length = 50)
    private String logradouro;
    @Column(name = "ds_endereco", length = 300)
    private String endereco;
    @Column(name = "ds_numero", length = 40)
    private String numero;
    @Column(name = "ds_complemento", length = 100)
    private String complemento;
    @Column(name = "ds_bairro", length = 100)
    private String bairro;
    @Column(name = "ds_cep", length = 50)
    private String cep;
    @JoinColumn(name = "id_cidade", referencedColumnName = "id", nullable = false)
    @ManyToOne
    private Cidade cidade;

    public Endereco() {
        this.id = -1;
        this.pessoa = new Pessoa();
        this.tipoEndereco = new TipoEndereco();
        this.logradouro = "";
        this.endereco = "";
        this.numero = "";
        this.complemento = "";
        this.bairro = "";
        this.cep = "";
        this.cidade = new Cidade();
    }

    public Endereco(int id, Pessoa pessoa, TipoEndereco tipoEndereco, String logradouro, String endereco, String numero, String complemento, String bairro, String cep, Cidade cidade) {
        this.id = id;
        this.pessoa = pessoa;
        this.tipoEndereco = tipoEndereco;
        this.logradouro = logradouro;
        this.endereco = endereco;
        this.numero = numero;
        this.complemento = complemento;
        this.bairro = bairro;
        this.cep = cep;
        this.cidade = cidade;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Pessoa getPessoa() {
        return pessoa;
    }

    public void setPessoa(Pessoa pessoa) {
        this.pessoa = pessoa;
    }

    public TipoEndereco getTipoEndereco() {
        return tipoEndereco;
    }

    public void setTipoEndereco(TipoEndereco tipoEndereco) {
        this.tipoEndereco = tipoEndereco;
    }

    public String getLogradouro() {
        return logradouro;
    }

    public void setLogradouro(String logradouro) {
        this.logradouro = logradouro;
    }

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getComplemento() {
        return complemento;
    }

    public void setComplemento(String complemento) {
        this.complemento = complemento;
    }

    public String getBairro() {
        return bairro;
    }

    public void setBairro(String bairro) {
        this.bairro = bairro;
    }

    public String getCep() {
        return cep;
    }

    public void setCep(String cep) {
        this.cep = cep;
    }

    public Cidade getCidade() {
        return cidade;
    }

    public void setCidade(Cidade cidade) {
        this.cidade = cidade;
    }
    
    

}
