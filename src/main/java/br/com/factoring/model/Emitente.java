/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.factoring.model;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 *
 * @author Claudemir Rtools
 */
@Entity
@Table(name = "pes_emitente")
public class Emitente implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;
    @Column(name = "ds_documento", length = 30)
    private String documento;
    @Column(name = "ds_nome", length = 200)
    private String nome;
    @JoinColumn(name = "id_tipo_documento", referencedColumnName = "id")
    @ManyToOne
    private TipoDocumento tipoDocumento;

    public Emitente() {
        this.id = -1;
        this.documento = "";
        this.nome = "";
        this.tipoDocumento = new TipoDocumento();
    }

    public Emitente(int id, String documento, String nome, TipoDocumento tipoDocumento) {
        this.id = id;
        this.documento = documento;
        this.nome = nome;
        this.tipoDocumento = tipoDocumento;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDocumento() {
        return documento;
    }

    public void setDocumento(String documento) {
        this.documento = documento;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public TipoDocumento getTipoDocumento() {
        return tipoDocumento;
    }

    public void setTipoDocumento(TipoDocumento tipoDocumento) {
        this.tipoDocumento = tipoDocumento;
    }

}
