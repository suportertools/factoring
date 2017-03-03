package br.com.factoring.model;

import java.io.Serializable;
import javax.persistence.*;

@Entity
@Table(name = "pes_cidade")
public class Cidade implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;
    @Column(name = "ds_cidade", length = 200)
    private String cidade;
    @Column(name = "ds_uf", length = 2)
    private String uf;

    public Cidade() {
        this.id = -1;
        this.cidade = "";
        this.uf = "";
    }

    public Cidade(int id, String cidade, String uf) {
        this.id = id;
        this.cidade = cidade;
        this.uf = uf;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCidade() {
        return cidade;
    }

    public void setCidade(String cidade) {
        this.cidade = cidade;
    }

    public String getUf() {
        return uf;
    }

    public void setUf(String uf) {
        this.uf = uf;
    }

}
