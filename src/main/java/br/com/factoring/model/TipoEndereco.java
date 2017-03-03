package br.com.factoring.model;

import java.io.Serializable;
import javax.persistence.*;

@Entity
@Table(name = "pes_tipo_endereco")
public class TipoEndereco implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;
    @Column(name = "ds_descricao", length = 20)
    private String descricao;

    public TipoEndereco() {
        this.id = -1;
        this.descricao = "";
    }

    public TipoEndereco(int id, String descricao) {
        this.id = id;
        this.descricao = descricao;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

}
