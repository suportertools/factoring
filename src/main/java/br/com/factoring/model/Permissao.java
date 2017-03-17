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
@Table(name = "seg_permissao")
public class Permissao implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;
    @Column(name = "ds_nome", length = 255)
    private String nome;
    @Column(name = "ds_acao", length = 255)
    private String acao;
    @JoinColumn(name = "id_pagina", referencedColumnName = "id")
    @ManyToOne
    private Pagina pagina;

    public Permissao() {
        this.id = -1;
        this.nome = "";
        this.acao = "";
        this.pagina = new Pagina();
    }

    public Permissao(int id, String nome, String acao, Pagina pagina) {
        this.id = id;
        this.nome = nome;
        this.acao = acao;
        this.pagina = pagina;
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

    public String getAcao() {
        return acao;
    }

    public void setAcao(String acao) {
        this.acao = acao;
    }

    public Pagina getPagina() {
        return pagina;
    }

    public void setPagina(Pagina pagina) {
        this.pagina = pagina;
    }

}
