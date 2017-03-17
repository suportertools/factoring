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
@Table(name = "seg_permissao_grupo")
public class PermissaoGrupo implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;
    @JoinColumn(name = "id_permissao", referencedColumnName = "id")
    @ManyToOne
    private Permissao permissao;
    @JoinColumn(name = "id_grupo", referencedColumnName = "id")
    @ManyToOne
    private Grupo grupo;

    public PermissaoGrupo() {
        this.id = -1;
        this.permissao = new Permissao();
        this.grupo = new Grupo();
    }

    public PermissaoGrupo(int id, Permissao permissao, Grupo grupo) {
        this.id = id;
        this.permissao = permissao;
        this.grupo = grupo;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Permissao getPermissao() {
        return permissao;
    }

    public void setPermissao(Permissao permissao) {
        this.permissao = permissao;
    }

    public Grupo getGrupo() {
        return grupo;
    }

    public void setGrupo(Grupo grupo) {
        this.grupo = grupo;
    }
}
