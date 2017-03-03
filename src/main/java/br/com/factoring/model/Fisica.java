package br.com.factoring.model;

import br.com.factoring.utils.Datas;
import java.io.Serializable;
import java.util.Date;
import javax.persistence.*;

@Entity
@Table(name = "pes_fisica")
public class Fisica implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;
    @Temporal(TemporalType.DATE)
    @Column(name = "dt_nascimento")
    private Date nascimento;
    @JoinColumn(name = "id_pessoa", referencedColumnName = "id", nullable = false)
    @ManyToOne
    private Pessoa pessoa;
    @Column(name = "ds_mae", length = 300)
    private String mae;
    @Column(name = "ds_pai", length = 300)
    private String pai;
    @Column(name = "ds_conjugue", length = 200)
    private String conjugue;
    @JoinColumn(name = "id_juridica", referencedColumnName = "id")
    @ManyToOne
    private Juridica juridica;

    public Fisica() {
        this.id = -1;
        this.nascimento = null;
        this.pessoa = new Pessoa();
        this.mae = "";
        this.pai = "";
        this.conjugue = "";
        this.juridica = null;
    }

    public Fisica(int id, Date nascimento, Pessoa pessoa, String mae, String pai, String conjugue, Juridica juridica) {
        this.id = id;
        this.nascimento = nascimento;
        this.pessoa = pessoa;
        this.mae = mae;
        this.pai = pai;
        this.conjugue = conjugue;
        this.juridica = juridica;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getNascimento() {
        return nascimento;
    }

    public void setNascimento(Date nascimento) {
        this.nascimento = nascimento;
    }

    public String getNascimentoString() {
        return Datas.converteData(nascimento);
    }

    public void setNascimentoString(String nascimentoString) {
        this.nascimento = Datas.converte(nascimentoString);
    }

    public Pessoa getPessoa() {
        return pessoa;
    }

    public void setPessoa(Pessoa pessoa) {
        this.pessoa = pessoa;
    }

    public String getMae() {
        return mae;
    }

    public void setMae(String mae) {
        this.mae = mae;
    }

    public String getPai() {
        return pai;
    }

    public void setPai(String pai) {
        this.pai = pai;
    }

    public String getConjugue() {
        return conjugue;
    }

    public void setConjugue(String conjugue) {
        this.conjugue = conjugue;
    }

    public Juridica getJuridica() {
        return juridica;
    }

    public void setJuridica(Juridica juridica) {
        this.juridica = juridica;
    }

}
