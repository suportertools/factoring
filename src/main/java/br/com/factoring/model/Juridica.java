package br.com.factoring.model;

import br.com.factoring.utils.Datas;
import java.io.Serializable;
import java.util.Date;
import javax.persistence.*;

@Entity
@Table(name = "pes_juridica")
public class Juridica implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;
    @JoinColumn(name = "id_pessoa", referencedColumnName = "id", nullable = false)
    @ManyToOne
    private Pessoa pessoa;
    @Column(name = "ds_inscricao_estadual", length = 300)
    private String inscricaoEstadual;

    public Juridica() {
        this.id = -1;
        this.pessoa = new Pessoa();
        this.inscricaoEstadual = "";
    }

    public Juridica(int id, Pessoa pessoa, String inscricaoEstadual) {
        this.id = id;
        this.pessoa = pessoa;
        this.inscricaoEstadual = inscricaoEstadual;
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

    public String getInscricaoEstadual() {
        return inscricaoEstadual;
    }

    public void setInscricaoEstadual(String inscricaoEstadual) {
        this.inscricaoEstadual = inscricaoEstadual;
    }

}
